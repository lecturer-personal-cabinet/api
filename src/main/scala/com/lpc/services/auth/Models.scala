package com.lpc.services.auth

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.{Json, _}

import scala.util.{Failure, Success, Try}

case class SystemUser(id: Option[Long],
                      loginInfo: LoginInfo,
                      email: String,
                      firstName: String,
                      lastName: String,
                      avatarUrl: Option[String],
                      activated: Boolean) extends Identity

object SystemUser {
  implicit val fmt: Format[SystemUser] = Json.format
}

