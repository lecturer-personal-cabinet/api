package com.lpc.services.auth

import cats.Monad
import cats.effect.IO
import com.lpc.database.DatabaseManager
import com.lpc.database.dao.{PasswordDao, UserDao}
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

trait UserService extends IdentityService[SystemUser] {
  def retrieve(loginInfo: LoginInfo): Future[Option[SystemUser]]

  def save(user: SystemUser): Future[SystemUser]
}

class UserServiceImpl[DB[_]] @Inject()(passwordDao: PasswordDao,
                                           userDao: UserDao[DB],
                                           dbManager: DatabaseManager[IO, DB])
                                          (implicit ec: ExecutionContext)
  extends UserService {

  def retrieve(loginInfo: LoginInfo): Future[Option[SystemUser]] = {
    dbManager.execute(userDao.retrieve(UserMapper.toEntity(loginInfo)))
      .unsafeToFuture()
      .map(_.map(user => UserMapper.toDto(user, UserMapper.toEntity(loginInfo))))
  }

  def save(user: SystemUser): Future[SystemUser] = {
    val userEntity = UserMapper.toEntity(user)
    val loginInfoEntity = UserMapper.toEntity(user.loginInfo)
    dbManager.execute(userDao.save(userEntity, loginInfoEntity))
      .unsafeToFuture()
      .map(user => UserMapper.toDto(user, loginInfoEntity))
  }
}