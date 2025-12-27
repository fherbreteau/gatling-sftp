package io.github.fherbreteau.gatling.sftp.model

import java.security.KeyPair

trait Credentials {

  def username: String

  def credential: Any
}

final case class PasswordAuth(username: String, password: String) extends Credentials {
  override def credential: Any = password
}

final case class KeyPairAuth(username: String, keyPair: KeyPair) extends Credentials {
  override def credential: Any = keyPair
}
