package com.lpc.services.auth

import com.lpc.database.mapping.{LoginInfoEntity, SystemUserEntity}
import com.mohiva.play.silhouette.api.LoginInfo

object UserMapper {
  def toDto(entity: SystemUserEntity, loginInfo: LoginInfoEntity) = SystemUser(
    id = entity.id,
    loginInfo = LoginInfo(loginInfo.providerID, loginInfo.providerKey),
    email = entity.email,
    firstName = entity.firstName,
    lastName = entity.lastName,
    avatarUrl = entity.avatarURL,
    activated = entity.activated)

  def toEntity(dto: SystemUser) = SystemUserEntity (
    id = dto.id,
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
