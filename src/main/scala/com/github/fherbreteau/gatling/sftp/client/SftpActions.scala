package com.github.fherbreteau.gatling.sftp.client

object SftpActions extends Enumeration {

  type Action = Value

  val Copy, Move, Delete, Upload, Download = Value

}