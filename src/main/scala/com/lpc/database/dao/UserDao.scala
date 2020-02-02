package com.lpc.database.dao

import cats.implicits._
import com.lpc.database.mapping.{LoginInfoTable, SystemUserEntity, _}
import slick.dbio._

trait UserDao[DB[_]] {
  def retrieve(loginInfo: LoginInfoEntity): DB[Option[SystemUserEntity]]

  def save(user: SystemUserEntity, loginInfo: LoginInfoEntity): DB[SystemUserEntity]

  def retrieveLoginInfo(loginInfo: LoginInfoEntity): DB[Option[LoginInfoEntity]]

  def insertLoginInfo(dbLoginInfo: LoginInfoEntity): DB[LoginInfoEntity]
}

class SlickUserDao extends UserDao[DBIO] {

  import slick.jdbc.PostgresProfile.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  override def retrieve(loginInfo: LoginInfoEntity): DBIO[Option[SystemUserEntity]] = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- UserLoginInfoTable.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- SystemUserTable.filter(_.id === dbUserLoginInfo.userId)
    } yield dbUser

    userQuery
      .result
      .headOption
  }

  override def retrieveLoginInfo(loginInfo: LoginInfoEntity): DBIO[Option[LoginInfoEntity]] =
    LoginInfoTable.filter(info => info.providerId === loginInfo.providerID &&
      info.providerKey === loginInfo.providerKey)
      .result
      .headOption

  override def insertLoginInfo(dbLoginInfo: LoginInfoEntity): DBIO[LoginInfoEntity] =
    LoginInfoTable
      .map(li => (li.providerId, li.providerKey))
      .returning(LoginInfoTable.map(_.id))
      .+=((dbLoginInfo.providerID, dbLoginInfo.providerKey))
      .map(newId => dbLoginInfo.copy(id = newId.some))

  override def save(user: SystemUserEntity, loginInfo: LoginInfoEntity): DBIO[SystemUserEntity] = {
    val loginInfoAction = {
      val retrieveLoginInfoAction = retrieveLoginInfo(loginInfo)
      val insertLoginInfoAction = insertLoginInfo(loginInfo)

      for {
        loginInfoOption <- retrieveLoginInfoAction
        loginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfoAction)
      } yield loginInfo
    }

    (for {
      idValue <- SystemUserTable
        .map(u => (u.firstName, u.lastName, u.email, u.avatarURL, u.activated))
        .returning(SystemUserTable.map(_.id))
        .+=((user.firstName, user.lastName, user.email, user.avatarURL, user.activated))
      loginInfo <- loginInfoAction
      _ <- UserLoginInfoTable += UserInfoEntity(idValue, loginInfo.id.get)
    } yield ())
      .transactionally
      .map(_ => user)
  }

  private def loginInfoQuery(loginInfo: LoginInfoEntity) =
    LoginInfoTable
      .filter(dbLoginInfo => dbLoginInfo.providerId === loginInfo.providerID &&
        dbLoginInfo.providerKey === loginInfo.providerKey)
}
