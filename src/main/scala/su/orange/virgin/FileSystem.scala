package su.orange.virgin

import java.io.File
import java.net.{URLConnection, URLEncoder}
import java.nio.charset.StandardCharsets

import akka.japi.Option.Some
import org.slf4j.{Logger, LoggerFactory}

class FileSystem(val settings: Settings) {
  val log = LoggerFactory.getLogger(this.getClass)
  def listFiles(path: String): Seq[FSObject] = {
    val dir = new File(settings.rootFolder + "/" + path)
    if (dir.exists && dir.isDirectory) {
      dir.listFiles.filter(isNotHidden).map(f => fsToVfile(f, settings.rootFolder)).map(res => {
        log.debug("Scanned file "+res)
        res
      })
    } else {
      List[FSObject]()
    }
  }

  private def isNotHidden(f: File) = !f.getName.startsWith(".")

  private def fsToVfile(file: File, rootFolder: String): FSObject = {
    if (file.isDirectory) {
      vFolder(file.getName,
        URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath, rootFolder), StandardCharsets.UTF_8.displayName())
      )
    } else {
      vFile(
        file.getName,
        URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath, rootFolder), StandardCharsets.UTF_8.displayName()),
        Some(URLConnection.guessContentTypeFromName(file.getName)).getOrElse("blob"),
        "http://" + settings.host + ":" + settings.port + "/files/" + URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath, rootFolder), StandardCharsets.UTF_8.displayName())
      )
    }
  }

  private def absolutePathToPathFromRootFolder(abs: String, root: String) = {
    abs.replace(root, "")
  }
}
