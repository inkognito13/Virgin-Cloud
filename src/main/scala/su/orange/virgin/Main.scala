package su.orange.virgin

import java.io.File

import akka.http.scaladsl.settings.ServerSettings
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]): Unit = {
    val config = if (args.isEmpty) {
      ConfigFactory.load()
    } else {
      val f = new File(args(0))
      if (f.canRead && !f.isDirectory) {
        val fileConfig = ConfigFactory.parseFile(f)
        ConfigFactory.load(fileConfig)
      } else {
        throw new IllegalArgumentException("File " + args(0) + " is not readable")
      }
    }
    val settings = new Settings(config)
    val fileSystem = new FileSystem(settings)
    val server = new HttpServer(fileSystem, settings)
    val serverSettings = ServerSettings(ConfigFactory.load).withVerboseErrorMessages(true)
    server.startServer("localhost", settings.port, serverSettings)
  }
}


