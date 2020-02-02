package com.lpc.database.mapping

import slick.lifted.MappedTo

final case class EntityKey(value: String) extends AnyVal with MappedTo[String]

case class SystemUserEntity(id: Option[EntityKey],
                            firstName: String,
                            lastName: String,
                            email: String,
                            avatarURL: Option[String],
                            activated: Boolean)

case class LoginInfoEntity(id: Option[EntityKey],
                           providerID: String,
                           providerKey: String)

case class UserInfoEntity(userID: EntityKey,
                          loginInfoId: EntityKey)

case class PasswordInfoEntity(hasher: String,
                              password: String,
                              salt: Option[String],
                              loginInfoId: EntityKey)
