package com.lpc.database.dao

import com.lpc.database.mapping._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

trait PasswordDao extends DelegableAuthInfoDAO[PasswordInfo] {}

class SlickPasswordDao @Inject()(dbConfig: DatabaseConfig[JdbcProfile])
                                (implicit ec: ExecutionContext, val classTag: ClassTag[PasswordInfo])
  extends PasswordDao {

  import slick.jdbc.PostgresProfile.api._

  implicit lazy val PwdInfoFormat: Format[PasswordInfo] = Json.format[PasswordInfo]

  override def remove(loginInfo: LoginInfo): Future[Unit] =
    dbConfig.db.run(passwordInfoSubQuery(loginInfo).delete).map(_ => ())

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val query = loginInfoQuery(loginInfo).joinLeft(PasswordInfoTable).on(_.id === _.loginInfoId)
    val action = query.result.head.flatMap {
      case (_, Some(_)) => updateAction(loginInfo, authInfo)
      case (_, None) => addAction(loginInfo, authInfo)
    }
    dbConfig.db.run(action).map(_ => authInfo)
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    dbConfig.db.run(updateAction(loginInfo, authInfo)).map(_ => authInfo)

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    dbConfig.db.run(addAction(loginInfo, authInfo)).map(_ => authInfo)

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    dbConfig.db.run(passwordInfoQuery(loginInfo).result.headOption).map { dbPasswordInfoOption =>
      dbPasswordInfoOption.map(dbPasswordInfo =>
        PasswordInfo(dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt))
    }

  private def passwordInfoQuery(loginInfo: LoginInfo) =
    for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbPasswordInfo <- PasswordInfoTable if dbPasswordInfo.loginInfoId === dbLoginInfo.id
    } yield dbPasswordInfo

  private def passwordInfoSubQuery(loginInfo: LoginInfo) =
    PasswordInfoTable.filter(_.loginInfoId in loginInfoQuery(loginInfo).map(_.id))

  private def addAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    loginInfoQuery(loginInfo).result.head.flatMap { dbLoginInfo =>
      PasswordInfoTable +=
        PasswordInfoEntity(authInfo.hasher, authInfo.password, authInfo.salt, dbLoginInfo.id.get)
    }.transactionally

  private def updateAction(loginInfo: LoginInfo, authInfo: PasswordInfo) =
    passwordInfoSubQuery(loginInfo)
      .map(dbPasswordInfo => (dbPasswordInfo.hasher, dbPasswordInfo.password, dbPasswordInfo.salt)).
      update((authInfo.hasher, authInfo.password, authInfo.salt))

  private def loginInfoQuery(loginInfo: LoginInfo) =
    LoginInfoTable.filter(dbLoginInfo => dbLoginInfo.providerId === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

}
