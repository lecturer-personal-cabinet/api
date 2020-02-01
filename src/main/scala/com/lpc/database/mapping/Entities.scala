package com.lpc.database.mapping

case class SystemUserEntity(id: Option[Long],
                            firstName: String,
                            lastName: String,
                            email: String,
                            avatarURL: Option[String],
                            activated: Boolean)

case class LoginInfoEntity(id: Option[Long],
                           providerID: String,
                           providerKey: String)

case class UserInfoEntity(userID: Long,
                          loginInfoId: Long)

case class PasswordInfoEntity(hasher: String,
                              password: String,
                              salt: Option[String],
                              loginInfoId: Long)
