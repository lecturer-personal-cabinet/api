package com.lpc.database

import com.lpc.database.mapping.SlickDbTableDefinitions.{LoginInfos, PasswordInfos, UserLoginInfos, Users}
import slick.lifted.TableQuery

package object mapping {
  lazy val SystemUserTable = TableQuery[Users]
  lazy val LoginInfoTable = TableQuery[LoginInfos]
  lazy val UserLoginInfoTable = TableQuery[UserLoginInfos]
  lazy val PasswordInfoTable = TableQuery[PasswordInfos]
}
