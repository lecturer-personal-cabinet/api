package com.lpc.controllers


import cats.effect.IO
import com.lpc.controllers.AuthenticationController.SignUpRequest
import com.lpc.services.auth.AuthenticationService.{AlreadyExists, SignUpResultData}
import com.lpc.services.auth.{AuthenticationService, DefaultEnv, Token}
import com.lpc.services.user.UserService
import com.mohiva.play.silhouette.api._
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.{Format, Json}
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext

class AuthenticationController @Inject()(components: ControllerComponents,
                                         silhouette: Silhouette[DefaultEnv],
                                         userService: UserService,
                                         authenticationService: AuthenticationService[IO])
                                        (implicit ex: ExecutionContext)
  extends LpcController(components)
    with I18nSupport {

  def signUp = jsonAsyncPost[SignUpRequest] { case (rh, request) =>
    authenticationService.signUp(request.identifier,
      request.password,
      request.email,
      request.firstName,
      request.lastName)(rh).map {
      case Left(AlreadyExists) => jsonFail(BadRequest)
      case Right(data: SignUpResultData) => jsonOk(Token(token = data.token, expiresOn = data.expiresOn))
    }
  }
}

object AuthenticationController {
  case class SignUpRequest(identifier: String,
                           password: String,
                           email: String,
                           firstName: String,
                           lastName: String)
  object SignUpRequest {
    implicit val fmt: Format[SignUpRequest] = Json.format
  }
}