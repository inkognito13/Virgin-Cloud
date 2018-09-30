package su.orange.virgin

import com.typesafe.config.Config

class Settings (conf:Config){
  def host = conf.getConfig("addr").getString("host")
  def port = conf.getConfig("addr").getInt("port")
  def isSsl = conf.getConfig("addr").getBoolean("ssl")
  def rootFolder = conf.getString("rootFolder")
}
