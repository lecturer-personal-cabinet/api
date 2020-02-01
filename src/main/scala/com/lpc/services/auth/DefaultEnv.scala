package com.lpc.services.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

trait DefaultEnv extends Env {
  type I = SystemUser
  type A = JWTAuthenticator
}

