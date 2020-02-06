package com.lpc.services.auth

import org.joda.time.DateTime
import play.api.libs.json.{JsObject, Json, OWrites}

case class Token(token: String, expiresOn: DateTime)

object Token {

  implicit object TokenWrites extends OWrites[Token] {
    def writes(token: Token): JsObject = {
      val json = Json.obj(
        "token" -> token.token,
        "expiresOn" -> token.expiresOn.toString
      )
      json
    }
  }

}