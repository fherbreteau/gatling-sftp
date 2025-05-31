package io.github.fherbreteau.gatling.sftp.client

import com.typesafe.scalalogging.StrictLogging
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.CoreComponents
import io.gatling.core.controller.throttle.Throttler
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.github.fherbreteau.gatling.sftp.client.result.{SftpFailure, SftpResponse, SftpResult}
import io.github.fherbreteau.gatling.sftp.model.{Credentials, KeyPairAuth, PasswordAuth}
import org.apache.sshd.client.SshClient
import org.apache.sshd.client.auth.UserAuthFactory
import org.apache.sshd.client.auth.password.UserAuthPasswordFactory
import org.apache.sshd.client.session.ClientSession
import org.apache.sshd.sftp.client.{SftpClient, SftpClientFactory}

import java.time.Duration.ofSeconds
import java.util.concurrent.{Executor, Executors}
import scala.util.control.NonFatal
import scala.jdk.CollectionConverters._

object Exchange {

  def apply(server: String, port: Int, credentials: Credentials): Exchange =
    Exchange(
      client = SshClient.setUpDefaultClient(),
      server = server,
      port = port,
      credentials = credentials,
      executor = Executors.newSingleThreadExecutor()
    )
}

final case class Exchange(var client: SshClient,
                          server: String,
                          port: Int,
                          credentials: Credentials,
                          executor: Executor) extends StrictLogging {
  def start(): Unit = {
    if (client.isClosed) {
      // If the SshClient was closed, create a new one.
      client = SshClient.setUpDefaultClient()
    }
    val authFactories: List[UserAuthFactory] = List(UserAuthPasswordFactory.INSTANCE)
    client.setUserAuthFactories(authFactories.asJava)
    client.start()
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
    var sshSession: ClientSession = null
    var sftpClient: SftpClient = null
    val result = try {
      logger.debug(s"Creating New Session scenario=${transaction.scenario} userId=${transaction.userId}")
      sshSession = client.connect(credentials.username, server, port)
        .verify(ofSeconds(5)).getSession
      credentials match {
        case _ @ PasswordAuth(_, password) => sshSession.addPasswordIdentity(password)
        case _ @ KeyPairAuth(_, keyPair) => sshSession.addPublicKeyIdentity(keyPair)
      }
      sshSession.auth()
        .verify(ofSeconds(5))
      sftpClient = SftpClientFactory.instance()
        .createSftpClient(sshSession)

      logger.debug(s"Opening SFTP Client scenario=${transaction.scenario} userId=${transaction.userId}")
      val executor = transaction.sftpOperation.build

      logger.debug(s"Executing operation=${transaction.sftpOperation.operationName} scenario=${transaction.scenario} userId=${transaction.userId}")
      executor.apply(sftpClient)

      logger.debug(s"Action ${transaction.action} successful")
      SftpResponse(transaction.action, startTime, clock.nowMillis, OK)
    } catch {
      case NonFatal(t) =>
        logger.error(s"Failed to execute action ${transaction.action}", t)
        SftpFailure(transaction.action, startTime, clock.nowMillis, t.getMessage, KO)
    } finally {
      if (sftpClient != null) sftpClient.close()
      if (sshSession != null) sshSession.close(false)
    }

    logger.debug(s"Sftp Operation completed scenario=${transaction.scenario} userId=${transaction.userId}")
    logResult(statsEngine, transaction.session, transaction.fullRequestName, result)
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