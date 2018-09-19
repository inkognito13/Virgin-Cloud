package su.orange.virgin

abstract class FSObject {
  def name: String

  def path: String

  def fType: String
}

final case class vFile(name: String, path: String, mediaType: String, url: String, fType: String = "file") extends FSObject

final case class vFolder(name: String, path: String, fType: String = "folder") extends FSObject
