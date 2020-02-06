package com.lpc.controllers

import com.lpc.services.auth.DefaultEnv
import com.mohiva.play.silhouette.api.Silhouette
import javax.inject.Inject
import play.api.mvc.ControllerComponents

class PingController @Inject() (cc: ControllerComponents, silhouette: Silhouette[DefaultEnv])
  extends LpcController(cc, silhouette) {

  def ping() = Action { implicit request =>
    jsonOk()
  }

  def authPing = silhouette.SecuredAction { implicit request =>
    jsonOk()
  }
}