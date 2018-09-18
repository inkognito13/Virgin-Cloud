package su.orange.virgin

import java.io.File
import java.net.{URLConnection, URLDecoder, URLEncoder}
import java.nio.charset.StandardCharsets

import akka.actor.{ActorRef, ActorSystem}
import akka.http.javadsl.server.Route
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Deflate
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes.MovedPermanently
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, HttpApp}
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.japi.Option.Some
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.{DefaultJsonProtocol, JsArray, JsObject, JsString, JsValue, RootJsonFormat}

import scala.io.StdIn

object Main {

  abstract class fsObject {
    def name: String

    def path: String

    def fType: String
  }

  final case class vFile(name: String, path: String, mediaType: String, url: String, fType: String = "file") extends fsObject

  final case class vFolder(name: String, path: String, fType: String = "folder") extends fsObject

  trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

    implicit object fsObjectJsonFormat extends RootJsonFormat[fsObject] {
      def write(f: fsObject) = {
        f.fType match {
          case "file" => vFileFormat.write(f.asInstanceOf[vFile])
          case "folder" => vFolderFormat.write(f.asInstanceOf[vFolder])
        }
      }

      //      JsArray(JsString(c.name), JsNumber(c.red), JsNumber(c.green), JsNumber(c.blue))

      def read(value: JsValue) = ???
    }

    implicit val vFileFormat = jsonFormat5(vFile)
    implicit val vFolderFormat = jsonFormat3(vFolder)
  }


  var host: String = ""
  var port: Int = 0
  var rootFolder: String = ""

  def main(args: Array[String]): Unit = {

    host = args(0)
    port = args(1).toInt
    rootFolder = args(2)


    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    class MyJsonService extends Directives with JsonSupport {
      val route = {
        pathPrefix("api" / RemainingPath) { filePath =>
          get {
            complete(listFiles(URLDecoder.decode(filePath.toString(), StandardCharsets.UTF_8.displayName()), rootFolder))
          }
        } ~
          pathPrefix("files" / RemainingPath) { filePath =>
            get {
              getFromDirectory(rootFolder + "/" + URLDecoder.decode(filePath.toString(), StandardCharsets.UTF_8.displayName()))
            }
          }
      }
    }

    val service = new MyJsonService

    val bindingFuture = Http().bindAndHandle(service.route, host, port)

    println("Server online at http://" + host + ":" + port + " with root folder set to " + rootFolder + "/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

  def listFiles(path: String, rootFolder: String): Seq[fsObject] = {
    val dir = new File(rootFolder + "/" + path)
    if (dir.exists && dir.isDirectory) {
      dir.listFiles.filter(isNotHidden).map(f => fsToVfile(f, rootFolder)).map(res => {println(res); res})
    } else {
      List[fsObject]()
    }
  }

  def isNotHidden(f: File) = !f.getName.startsWith(".")


  def fsToVfile(file: File, rootFolder: String): fsObject = {
    if (file.isDirectory) {
      vFolder(file.getName,
        URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath, rootFolder), StandardCharsets.UTF_8.displayName())
      )
    } else {
      vFile(
        file.getName,
        URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath, rootFolder), StandardCharsets.UTF_8.displayName()),
        Some(URLConnection.guessContentTypeFromName(file.getName)).getOrElse("blob"),
        "http://" + host + ":" + port + "/files/" + URLEncoder.encode(absolutePathToPathFromRootFolder(file.getAbsolutePath, rootFolder), StandardCharsets.UTF_8.displayName())
      )
    }
  }

  def absolutePathToPathFromRootFolder(abs: String, root: String) = {
    abs.replace(root, "")
  }

}


