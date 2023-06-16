package io.github.fherbreteau.gatling.sftp.client

import io.gatling.commons.validation.Validation
import io.gatling.core.Predef.Session
import io.gatling.core.session.Expression
import io.github.fherbreteau.gatling.sftp.client.SftpActions.{Action, Copy, Delete, Download, Move, Upload}
import io.github.fherbreteau.gatling.sftp.protocol.SftpProtocol
import org.apache.sshd.common.util.io.IoUtils
import org.apache.sshd.sftp.client.SftpClient
import org.apache.sshd.sftp.client.SftpClient.OpenMode

import java.nio.file.Files
import scala.util.Using

final case class SftpOperationDef(operationName: Expression[String],
                                  operationDef: Expression[OperationDef],
                                  sftpProtocol: SftpProtocol,
                                  throttled: Boolean) {
  def build(session: Session): Validation[SftpOperation] =
    operationDef(session).map(definition => SftpOperation(definition, sftpProtocol, throttled))
}

object SftpOperation {
  def apply(definition: OperationDef, sftpProtocol: SftpProtocol, throttled: Boolean): SftpOperation =
    SftpOperation(definition.operationName, definition, sftpProtocol, throttled)
}

final case class SftpOperation(operationName: String,
                               definition: OperationDef,
                               sftpProtocol: SftpProtocol,
                               throttled: Boolean) {

  def build: SftpClient => Unit = {
    val localSourcePath = sftpProtocol.source(definition.file, isLocal = true)
    val localDestPath = sftpProtocol.destination(definition.file, isLocal = true)
    val remoteSourcePath = sftpProtocol.source(definition.file, isLocal = false).toString
    val remoteDestPath = sftpProtocol.destination(definition.file, isLocal = false).toString
    definition.action match {
      case Move => client => {
        client.rename(remoteSourcePath, remoteDestPath)
      }
      case Copy => client => {
        Using.Manager { use =>
          val source = use(client.read(remoteSourcePath))
          val destination = use(client.write(remoteDestPath, OpenMode.Create))
          IoUtils.copy(source, destination)
        }
      }
      case Delete => client => {
        client.remove(remoteSourcePath)
      }
      case Download => client => {
        Using.Manager { use =>
          val source = use(client.read(remoteSourcePath))
          val destination = use(Files.newOutputStream(localDestPath))
          IoUtils.copy(source, destination)
        }
      }
      case Upload => client => {
        Using.Manager { use =>
          val source = use(Files.newInputStream(localSourcePath))
          val destination = use(client.write(remoteDestPath, OpenMode.Create))
          IoUtils.copy(source, destination)
        }
      }
    }
  }

}

final case class OperationDef(operationName: String,
                              file: String,
                              action: Action) {}
