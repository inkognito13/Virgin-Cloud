package su.orange.virgin

import akka.http.scaladsl.settings.ServerSettings
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1).toInt
    val rootFolder = args(2)
    val settings = Settings(host, port, rootFolder)
    val fileSystem = new FileSystem(settings)
    val server = new HttpServer(fileSystem, settings)
    val serverSettings = ServerSettings(ConfigFactory.load).withVerboseErrorMessages(true)
    server.startServer("localhost", port, serverSettings)
  }
}


