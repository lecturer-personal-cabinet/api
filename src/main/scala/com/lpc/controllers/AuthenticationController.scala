package com.lpc.controllers


import com.lpc.controllers.AuthenticationController.SignUpRequest
import com.lpc.services.auth.UserService
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.{Format, Json}
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

class AuthenticationController @Inject()(components: ControllerComponents,
                                         userService: UserService)
                                        (implicit ex: ExecutionContext)
  extends LpcController(components)
    with I18nSupport {

  def signUp = jsonAsyncPost[SignUpRequest] { case (_, request) =>
    println("Sign up request: " + request)
    val loginInfo = LoginInfo(CredentialsProvider.ID, request.identifier)
    userService.retrieve(loginInfo).flatMap {
      case None => Future.successful(jsonOk("Not exists"))
      case Some(_) => Future.successful(jsonOk("Exists"))
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