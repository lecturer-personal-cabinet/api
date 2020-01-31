package com.lpc.controllers

import javax.inject.Inject
import play.api.mvc.ControllerComponents

class PingController @Inject() (cc: ControllerComponents) extends LpcController(cc) {
  def ping() = Action { implicit request =>
    jsonOk()
  }
}