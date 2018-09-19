package su.orange.virgin

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, HttpApp, Route}

import scala.concurrent.{ExecutionContext, Future, Promise, blocking}

class HttpServer(fs: FileSystem, settings: Settings) extends HttpApp {

  class RoutingService extends Directives with JsonSupport {
    val route = {
      path("api") {
        get {
          complete(fs.listFiles(""))
        }
      } ~
        pathPrefix("api" / RemainingPath) { filePath =>
          get {
            complete(fs.listFiles(URLDecoder.decode(filePath.toString(), StandardCharsets.UTF_8.displayName())))
          }
        } ~
        pathPrefix("files" / RemainingPath) { filePath =>
          get {
            getFromDirectory(settings.rootFolder + "/" + URLDecoder.decode(filePath.toString(), StandardCharsets.UTF_8.displayName()))
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
