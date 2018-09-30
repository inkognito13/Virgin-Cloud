package su.orange.virgin

import java.io.File
import java.net.{URLConnection, URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets

import akka.japi.Option.Some
import org.slf4j.LoggerFactory

class FileSystem(settings: Settings) {
  val log = LoggerFactory.getLogger(this.getClass)
  private final val UTF_8 = StandardCharsets.UTF_8.displayName()

  def listFiles(remotePath: String): Seq[FSObject] = {
    val localPath = getLocalFilePath(remotePath)
    log.debug("Listing files in " + localPath)
    val dir = new File(localPath)
    if (dir.exists && dir.isDirectory) {
      val res = dir.listFiles.filter(isNotHidden).map(f => fsToVFile(f, settings.rootFolder))
      log.debug("Found " + res.length + " files in " + localPath)
      log.trace(res.map(_.toString).mkString(", \n"))
      res
    } else {
      throw new IllegalArgumentException(localPath + " is not a directory or not exists")
    }
  }

  private def getLocalFilePath(remoteFilePath: String) =
    settings.rootFolder + "/" + URLDecoder.decode(remoteFilePath, UTF_8)
  
  def getFilePathResponse(remoteFilePath: String) =
    getLocalFilePath(remoteFilePath)

  private def isNotHidden(f: File) = !f.getName.startsWith(".")

  private def fsToVFile(file: File, rootFolder: String): FSObject = {
    if (file.isDirectory) {
      vFolder(file.getName, encodedFilePath(file))
    } else {
      vFile(file.getName, encodedFilePath(file), calculateContentType(file), buildFileUrl(file))
    }
  }

  private def encodedFilePath(file: File) =
    URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath), UTF_8)

  private def absolutePathToPathFromRootFolder(abs: String) =
    abs.replace(settings.rootFolder, "")

  private def calculateContentType(file: File) =
    Some(URLConnection.guessContentTypeFromName(file.getName)).getOrElse("blob")

  private def buildFileUrl(file: File) =
    (if (settings.isSsl) "https://" else "http://") + settings.host + ":" + settings.port + "/files/" + encodedFilePath(file)

}
