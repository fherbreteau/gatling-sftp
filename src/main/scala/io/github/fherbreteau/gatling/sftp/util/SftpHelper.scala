package io.github.fherbreteau.gatling.sftp.util

import com.typesafe.scalalogging.StrictLogging
import io.gatling.core.session.{Expression, Session}
import io.github.fherbreteau.gatling.sftp.model.{Credentials, KeyPairAuth, PasswordAuth}
import org.apache.sshd.common.config.keys.FilePasswordProvider
import org.apache.sshd.common.util.security.SecurityUtils

import java.io.InputStream
import java.security.KeyPair
import scala.util.Using

object SftpHelper extends StrictLogging {
  def buildPasswordAuth(username: Expression[String], password: Expression[String]): Expression[Credentials] =
    (session : Session) =>
      for {
        usernameValue <- username(session)
        passwordValue <- password(session)
      } yield PasswordAuth(usernameValue, passwordValue)

  def buildKeyPairAuth(username: Expression[String], keyPath: Expression[String]): Expression[Credentials] =
    (session: Session) =>
      for {
        usernameValue <- username(session)
        keyPairValue <- keyPath(session).map(path => loadKeyPair(path))
      } yield KeyPairAuth(usernameValue, keyPairValue)


  def buildKeyPairAuth(username: Expression[String], keyPath: Expression[String], keyPassphrase: Expression[String]): Expression[Credentials] =
    (session: Session) =>
      for {
        usernameValue <- username(session)
        keyPassphraseValue <- keyPassphrase(session)
        keyPairValue <- keyPath(session).map(path => loadKeyPair(path, keyPassphraseValue))
      } yield KeyPairAuth(usernameValue, keyPairValue)

  private def loadKeyPair(path: String) : KeyPair =
    Using.Manager { use =>
      val stream: InputStream = use(getClass.getResourceAsStream(path))
      SecurityUtils.loadKeyPairIdentities(null, null, stream, FilePasswordProvider.EMPTY).iterator().next()
    }.get

  private def loadKeyPair(path: String, keyPassphrase: String) : KeyPair =
    Using.Manager { use =>
      val stream: InputStream = use(getClass.getResourceAsStream(path))
      SecurityUtils.loadKeyPairIdentities(null, null, stream, FilePasswordProvider.of(keyPassphrase)).iterator().next()
    }.get

}
