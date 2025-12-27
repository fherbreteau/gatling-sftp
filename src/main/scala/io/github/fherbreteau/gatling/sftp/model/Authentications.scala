package io.github.fherbreteau.gatling.sftp.model

object Authentications extends Enumeration {
  type Authentication = Value

  val Password, KeyPair = Value
}