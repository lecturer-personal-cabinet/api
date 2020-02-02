package com.lpc.services.auth

import cats.Monad
import cats.effect.{ContextShift, IO}
import com.lpc.services.auth.AuthenticationService.{AlreadyExists, SignUpFailedResponse, SignUpResultData, SignUpSuccessResponse}
import com.lpc.services.user.{SystemUser, UserService}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import javax.inject.Inject
import org.joda.time.DateTime
import play.api.mvc.RequestHeader

import scala.concurrent.Future

trait AuthenticationService[F[_]] {
  def signUp(identifier: String,
             password: String,
             email: String,
             firstName: String,
             lastName: String)
            (implicit request: RequestHeader): Future[Either[SignUpFailedResponse, SignUpSuccessResponse]]
}

class AuthenticationServiceImpl[F[_]: Monad] @Inject()(userService: UserService,
                                                       passwordHasherRegistry: PasswordHasherRegistry,
                                                       authInfoRepository: AuthInfoRepository,
                                                       silhouette: Silhouette[DefaultEnv])
                                                      (implicit cs: ContextShift[IO])
  extends AuthenticationService[F] {
  import cats.implicits._

  import scala.concurrent.ExecutionContext.Implicits.global

  override def signUp(identifier: String,
                      password: String,
                      email: String,
                      firstName: String,
                      lastName: String)
                     (implicit request: RequestHeader): Future[Either[SignUpFailedResponse, SignUpSuccessResponse]] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, identifier)
    userService.retrieve(loginInfo).flatMap {
      case None =>
        val authInfo = passwordHasherRegistry.current.hash(password)
        val systemUser = SystemUser(None, loginInfo, email, firstName, lastName, avatarUrl = None, activated = true)

        val result = for {
          _ <- userService.save(systemUser)
          _ <- authInfoRepository.add(loginInfo, authInfo)
          authenticator <- silhouette.env.authenticatorService.create(loginInfo)
          token <- silhouette.env.authenticatorService.init(authenticator)
          expiresOn = authenticator.expirationDateTime
        } yield SignUpResultData(token, expiresOn)

        silhouette.env.eventBus.publish(SignUpEvent(systemUser, request))
        silhouette.env.eventBus.publish(LoginEvent(systemUser, request))

        result.map(_.asRight)
      case Some(_) => Future.successful(AlreadyExists.asLeft)
    }
  }
}

object AuthenticationService {
  sealed trait SignUpSuccessResponse
  case class SignUpResultData(token: DefaultEnv#A#Value, expiresOn: DateTime) extends SignUpSuccessResponse

  sealed trait SignUpFailedResponse
  case object AlreadyExists extends SignUpFailedResponse

}