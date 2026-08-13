package io.github.fherbreteau.gatling.sftp.client

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.CoreComponents
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.github.fherbreteau.gatling.sftp.client.result.{SftpFailure, SftpResponse, SftpResult}
import io.github.fherbreteau.gatling.sftp.model.Authentications.{Authentication, KeyPair, Password}
import io.github.fherbreteau.gatling.sftp.model.{Credentials, KeyPairAuth, PasswordAuth}
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.auth.UserAuthFactory
import org.apache.sshd.client.auth.password.UserAuthPasswordFactory
import org.apache.sshd.client.auth.pubkey.UserAuthPublicKeyFactory
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.sftp.client.{SftpClient, SftpClientFactory}

import java.time.Duration.ofSeconds
import java.util.concurrent.{Executor, Executors}
import scala.jdk.CollectionConverters._
import scala.util.Using
import scala.util.control.NonFatal

object Exchange {

  def apply(server: String, port: Int, authType: Authentication): Exchange =
    Exchange(
      client = SshClient.setUpDefaultClient(),
      server = server,
      port = port,
      authType = authType,
      executor = Executors.newSingleThreadExecutor(),
      enableSessionPooling = false,
      maxPooledSessions = 5
    )

  def apply(server: String, port: Int, authType: Authentication, threadPoolSize: Int): Exchange =
    Exchange(
      client = SshClient.setUpDefaultClient(),
      server = server,
      port = port,
      authType = authType,
      executor = Executors.newFixedThreadPool(threadPoolSize),
      enableSessionPooling = false,
      maxPooledSessions = 5
    )
}

final case class Exchange(client: SshClient,
                          server: String,
                          port: Int,
                          authType: Authentication,
                          executor: Executor,
                          enableSessionPooling: Boolean,
                          maxPooledSessions: Int) extends StrictLogging {
  private def maskSensitiveInfo(message: String): String = {
    message
      .replaceAll("password[^\\w]*=.*(?=\\s|$)", "password=***")
      .replaceAll("passphrase[^\\w]*=.*(?=\\s|$)", "passphrase=***")
      .replaceAll("secret[^\\w]*=.*(?=\\s|$)", "secret=***")
      .replaceAll("key[^\\w]*=.*(?=\\s|$)", "key=***")
  }
  def start(): Exchange = {
    val currentClient = if (client.isClosed) {
      // If the SshClient was closed, create a new one.
      SshClient.setUpDefaultClient()
    } else {
      client
    }

    authType match {
      case Password =>
        val authFactories: List[UserAuthFactory] = List(UserAuthPasswordFactory.INSTANCE)
        currentClient.setUserAuthFactories(authFactories.asJava)
      case KeyPair =>
        val authFactories: List[UserAuthFactory] = List(UserAuthPublicKeyFactory.INSTANCE)
        currentClient.setUserAuthFactories(authFactories.asJava)
    }
    currentClient.start()
    this.copy(client = currentClient)
  }

  def stop(): Unit = client.stop()

  def execute(transaction: SftpTransaction, coreComponents: CoreComponents): Unit = {
    logger.debug(s"Sending operation=${transaction.fullRequestName} server=${transaction.server} scenario=${transaction.scenario} userId=${transaction.userId}")
    coreComponents.throttler match {
      case Some(th) if transaction.throttled =>
        th ! Throttler.Command.ThrottledRequest(
          transaction.scenario,
          () => executeOperation(transaction, coreComponents)
        )
      case _ => executeOperation(transaction, coreComponents)
    }
  }

  private def executeOperation(transaction: SftpTransaction, coreComponents: CoreComponents): Unit = {
    // Execute the Sftp operation to not block the virtual user thread.
    executor.execute(() => executeOperationAsync(transaction, coreComponents))
  }

  private def executeOperationAsync(transaction: SftpTransaction, coreComponents: CoreComponents): Unit = {
    import coreComponents._
    val startTime = clock.nowMillis
    Using.Manager { use =>
      logger.debug(s"Creating New Session scenario=${transaction.scenario} userId=${transaction.userId}")
      val credential: Credentials = transaction.sftpOperation.sftpProtocol.credentials(transaction.session)
      val holder = client.connect(credential.username, server, port)
        .verify(ofSeconds(5))
      val sshSession: ClientSession = use(holder.getSession)
      credential match {
        case _ @ PasswordAuth(_, password) =>
          logger.debug(s"Logging using given password scenario=${transaction.scenario} userId=${transaction.userId}")
          sshSession.addPasswordIdentity(password)
        case _ @ KeyPairAuth(_, keyPair) =>
          logger.debug(s"Logging using given key pair scenario=${transaction.scenario} userId=${transaction.userId}")
          sshSession.addPublicKeyIdentity(keyPair)
      }
      sshSession.auth()
        .verify(ofSeconds(5))
      val sftpClient: SftpClient = use(SftpClientFactory.instance()
        .createSftpClient(sshSession))

      logger.debug(s"Opening SFTP Client scenario=${transaction.scenario} userId=${transaction.userId}")
      val executor = transaction.sftpOperation.build

      logger.debug(s"Executing operation=${transaction.sftpOperation.operationName} scenario=${transaction.scenario} userId=${transaction.userId}")
      executor.apply(sftpClient)

      logger.debug(s"Action ${transaction.action} successful")
      SftpResponse(transaction.action, startTime, clock.nowMillis, OK)
    }.recover {
      case NonFatal(t) =>
        logger.error(maskSensitiveInfo(s"Failed to execute action ${transaction.action}: ${t.getMessage}"), t)
        SftpFailure(transaction.action, startTime, clock.nowMillis, maskSensitiveInfo(t.getMessage), KO)
    }.map(result => {
      logger.debug(s"Sftp Operation completed with success ${result.status} scenario=${transaction.scenario} userId=${transaction.userId}")
      logResult(statsEngine, transaction.session, transaction.fullRequestName, result)
    })

    transaction.next ! transaction.session
  }

  private def logResult(statsEngine: StatsEngine, session: Session, fullRequestName: String, result: SftpResult): Unit = {
    statsEngine.logResponse(
      session.scenario,
      session.groups,
      fullRequestName,
      result.startTimestamp,
      result.endTimestamp,
      result.status,
      None,
      result.message
    )
  }
}