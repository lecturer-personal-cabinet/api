package com.lpc.services.user

import com.lpc.database.mapping.{EntityKey, LoginInfoEntity, SystemUserEntity}
import com.mohiva.play.silhouette.api.LoginInfo

object UserMapper {
  def toDto(entity: SystemUserEntity, loginInfo: LoginInfoEntity) = SystemUser(
    id = entity.id.map(_.value),
    loginInfo = LoginInfo(loginInfo.providerID, loginInfo.providerKey),
    email = entity.email,
    firstName = entity.firstName,
    lastName = entity.lastName,
    avatarUrl = entity.avatarURL,
    activated = entity.activated)

  def toEntity(dto: SystemUser) = SystemUserEntity (
    id = dto.id.map(id => EntityKey(id)),
    firstName = dto.firstName,
    lastName = dto.lastName,
    email = dto.email,
    avatarURL = dto.avatarUrl,
    activated = dto.activated)

  def toDto(loginInfoEntity: LoginInfoEntity) = LoginInfo(
    loginInfoEntity.providerID,
    loginInfoEntity.providerKey)

  def toEntity(loginInfo: LoginInfo) = LoginInfoEntity (
    None,
    loginInfo.providerID,
    loginInfo.providerKey)
}
