package com.lpc.database.mapping

import slick.lifted.ProvenShape

object SlickDbTableDefinitions {

  import slick.jdbc.PostgresProfile.api._

  class Users(tag: Tag) extends Table[SystemUserEntity](tag, "system_user") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def firstName: Rep[String] = column[String]("first_name")

    def lastName: Rep[String] = column[String]("last_name")

    def email: Rep[String] = column[String]("email")

    def avatarURL: Rep[Option[String]] = column[Option[String]]("avatar_url")

    def activated: Rep[Boolean] = column[Boolean]("activated")

    override def * : ProvenShape[SystemUserEntity] = (id.?, firstName, lastName, email, avatarURL, activated) <> (SystemUserEntity.tupled, SystemUserEntity.unapply)
  }

  class LoginInfos(tag: Tag) extends Table[LoginInfoEntity](tag, "login_info") {
    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def providerId: Rep[String] = column[String]("provider_id")

    def providerKey: Rep[String] = column[String]("provider_key")

    def * : ProvenShape[LoginInfoEntity] = (id.?, providerId, providerKey) <> (LoginInfoEntity.tupled, LoginInfoEntity.unapply)
  }

  class UserLoginInfos(tag: Tag) extends Table[UserInfoEntity](tag, "user_login_info") {
    def userId: Rep[Long] = column[Long]("user_id")

    def loginInfoId: Rep[Long] = column[Long]("login_info_id")

    def * : ProvenShape[UserInfoEntity] = (userId, loginInfoId) <> (UserInfoEntity.tupled, UserInfoEntity.unapply)
  }

  class PasswordInfos(tag: Tag) extends Table[PasswordInfoEntity](tag, "password_info") {
    def hasher: Rep[String] = column[String]("hasher")

    def password: Rep[String] = column[String]("password")

    def salt: Rep[Option[String]] = column[Option[String]]("salt")

    def loginInfoId: Rep[Long] = column[Long]("login_info_id")

    def * : ProvenShape[PasswordInfoEntity] = (hasher, password, salt, loginInfoId) <> (PasswordInfoEntity.tupled, PasswordInfoEntity.unapply)
  }
}
