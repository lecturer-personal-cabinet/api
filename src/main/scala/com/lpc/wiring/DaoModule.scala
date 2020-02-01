package com.lpc.wiring

import com.lpc.database.dao.{PasswordDao, SlickPasswordDao, SlickUserDao}
import com.softwaremill.macwire._
import play.api.db.slick.{DatabaseConfigProvider, DbName, SlickComponents}
import slick.jdbc.JdbcProfile

trait DaoModule extends SlickComponents {
  import scala.concurrent.ExecutionContext.Implicits.global

  lazy val dbConfig = slickApi.dbConfig[JdbcProfile](DbName("default"))
  lazy val PasswordDAO = wire[SlickPasswordDao]
  lazy val UserDao = wire[SlickUserDao]
}