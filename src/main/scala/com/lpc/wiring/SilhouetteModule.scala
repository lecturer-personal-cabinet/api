package com.lpc.wiring

import akka.actor.ActorSystem
import cats.effect.{ContextShift, IO}
import com.lpc.database.{DatabaseManager, SlickDatabaseManager}
import com.lpc.services.auth.{AuthenticationService, AuthenticationServiceImpl, DefaultEnv}
import com.lpc.services.user.{UserService, UserServiceImpl}
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.crypto.Base64AuthenticatorEncoder
import com.mohiva.play.silhouette.api.services.AuthenticatorService
import com.mohiva.play.silhouette.api.util.{Clock, PasswordHasher, PasswordHasherRegistry}
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, JWTAuthenticatorService, JWTAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, SocialProviderRegistry}
import com.mohiva.play.silhouette.impl.util.SecureRandomIDGenerator
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import play.api.mvc.BodyParsers
import play.api.{BuiltInComponents, Configuration}
import slick.dbio.DBIO

import scala.concurrent.duration.{Duration, FiniteDuration}

trait SilhouetteModule extends DaoModule with BuiltInComponents {
  import com.softwaremill.macwire._

  def configuration: Configuration

  implicit val system: ActorSystem = ActorSystem()
  implicit val cs: ContextShift[IO] = IO.contextShift(executionContext)
  lazy val DbManager: DatabaseManager[IO, DBIO] = wire[SlickDatabaseManager[IO, DBIO]]

  lazy val userService: UserService = wire[UserServiceImpl[DBIO]]
  lazy val authService: AuthenticationService[IO] = wire[AuthenticationServiceImpl[IO]]

  lazy val bpDefault: BodyParsers.Default = wire[BodyParsers.Default]

  lazy val clock: Clock = wire[Clock]
  lazy val authenticatorDecoder: Base64AuthenticatorEncoder = wire[Base64AuthenticatorEncoder]
  lazy val idGenerator = new SecureRandomIDGenerator()
  lazy val eventBus: EventBus = wire[EventBus]

  lazy val authenticatorService: AuthenticatorService[JWTAuthenticator] = {
    val duration = configuration.underlying.getString("silhouette.jwt.authenticator.authenticatorExpiry")
    val expiration = Duration.apply(duration).asInstanceOf[FiniteDuration]
    val config = new JWTAuthenticatorSettings(fieldName = configuration.underlying.getString("silhouette.jwt.authenticator.headerName"),
      issuerClaim = configuration.underlying.getString("silhouette.jwt.authenticator.issuerClaim"),
      authenticatorExpiry = expiration,
      sharedSecret = configuration.underlying.getString("silhouette.jwt.authenticator.sharedSecret"))
    new JWTAuthenticatorService(config, None, authenticatorDecoder, idGenerator, clock)
  }

  private lazy val env: Environment[DefaultEnv] = Environment[DefaultEnv](
    userService, authenticatorService, List(), eventBus
  )

  lazy val securedErrorHandler: SecuredErrorHandler = wire[DefaultSecuredErrorHandler]
  lazy val unSecuredErrorHandler: UnsecuredErrorHandler = wire[DefaultUnsecuredErrorHandler]

  lazy val securedRequestHandler : SecuredRequestHandler = wire[DefaultSecuredRequestHandler]
  lazy val unsecuredRequestHandler : UnsecuredRequestHandler = wire[DefaultUnsecuredRequestHandler]
  lazy val userAwareRequestHandler : UserAwareRequestHandler = wire[DefaultUserAwareRequestHandler]

  lazy val securedAction: SecuredAction = wire[DefaultSecuredAction]
  lazy val unsecuredAction: UnsecuredAction = wire[DefaultUnsecuredAction]
  lazy val userAwareAction: UserAwareAction = wire[DefaultUserAwareAction]

  lazy val authInfoRepository = new DelegableAuthInfoRepository(PasswordDAO)
  lazy val bCryptPasswordHasher: PasswordHasher = new BCryptPasswordHasher
  lazy val passwordHasherRegistry: PasswordHasherRegistry = new PasswordHasherRegistry(bCryptPasswordHasher)

  lazy val credentialsProvider = new CredentialsProvider(authInfoRepository, passwordHasherRegistry)

  lazy val socialProviderRegistry = new SocialProviderRegistry(List())

  lazy val silhouetteDefaultEnv : Silhouette[DefaultEnv] = wire[SilhouetteProvider[DefaultEnv]]
}