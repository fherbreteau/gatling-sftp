package io.github.fherbreteau.gatling.sftp.client

object SftpActions extends Enumeration {

  type Action = Value

  val Ls, Copy, Move, Delete, Upload, Download, Mkdir, RmDir = Value

}