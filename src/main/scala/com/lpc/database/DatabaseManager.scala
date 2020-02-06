package com.lpc.database

import cats.Monad
import cats.effect.{ContextShift, IO}
import javax.inject.Inject
import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

trait DatabaseManager[F[_], DB[_]] {
  def execute[A](action: DB[A]): F[A]
}

class SlickDatabaseManager[F[_] : Monad, DB[_]] @Inject() (dbConfig: DatabaseConfig[JdbcProfile])
                                                          (implicit cs: ContextShift[IO])
  extends DatabaseManager[IO, DBIO] {

  override def execute[A](action: DBIO[A]): IO[A] = IO.fromFuture(IO(dbConfig.db.run(action)))
}