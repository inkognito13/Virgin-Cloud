package su.orange.virgin

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object fsObjectJsonFormat extends RootJsonFormat[FSObject] {
    def write(f: FSObject) = {
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
