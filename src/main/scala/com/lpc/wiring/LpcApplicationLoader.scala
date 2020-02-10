package com.lpc.wiring

import com.lpc.controllers.{AuthenticationController, PingController}
import com.softwaremill.macwire.wire
import controllers.AssetsComponents
import play.api.ApplicationLoader.Context
import play.api.i18n.I18nComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import play.filters.cors.{CORSConfig, CORSFilter}
import router.Routes

class LpcApplicationLoader extends ApplicationLoader {
  override def load(context: ApplicationLoader.Context): Application = new LpcComponent(context).application
}

class LpcComponent (context: Context) extends BuiltInComponentsFromContext(context)
  with AssetsComponents
  with SilhouetteModule
  with HttpFiltersComponents
  with I18nComponents {

  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment, context.initialConfiguration, Map.empty)
  }

  lazy val router: Router = {
    val prefix: String = "/"
    wire[Routes]
  }

  lazy val PingController: PingController = wire[PingController]
  lazy val SignUpController: AuthenticationController = wire[AuthenticationController]

  lazy val corsFilter = CORSFilter(CORSConfig.fromConfiguration(configuration))

  override def httpFilters: Seq[EssentialFilter] = Seq(corsFilter)
}