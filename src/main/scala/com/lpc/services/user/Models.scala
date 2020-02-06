package com.lpc.services.user

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.{Format, Json}

case class SystemUser(id: Option[String],
                      loginInfo: LoginInfo,
                      email: String,
                      firstName: String,
                      lastName: String,
                      avatarUrl: Option[String],
                      activated: Boolean) extends Identity

object SystemUser {
  implicit val fmt: Format[SystemUser] = Json.format
}

