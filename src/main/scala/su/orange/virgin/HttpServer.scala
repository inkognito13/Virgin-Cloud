package su.orange.virgin

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, HttpApp, Route}

import scala.concurrent.{ExecutionContext, Future, Promise, blocking}
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

class HttpServer(fs: FileSystem, settings: Settings) extends HttpApp {

  class RoutingService extends Directives with JsonSupport {
    val route = cors() {
      pathPrefix("api") {
        path("fs") {
          get {
            complete(fs.listFiles(""))
          }
        } ~
          pathPrefix("fs" / RemainingPath) { filePath =>
            get {
              complete(fs.listFiles(filePath.toString()))
            }
          }

      } ~
        pathPrefix("files" / RemainingPath) { filePath =>
          get {
            getFromDirectory(fs.getLocalFilePath(filePath.toString()))
          }
        }
    }
  }

  override protected def routes: Route = new RoutingService().route

  override protected def waitForShutdownSignal(system: ActorSystem)(implicit ec: ExecutionContext): Future[Done] = {
    val promise = Promise[Done]()
    sys.addShutdownHook {
      promise.trySuccess(Done)
    }
    Future {
      blocking {
      }
    }
    promise.future
  }
}
