package com.skeleton.utils.validators

import cats.implicits._
import com.skeleton.service.user.UserModel.UserCreate

sealed trait ObjectsValidators {

  def validateUserCreate(userCreate: UserCreate): InputValidators.ValidationResult[UserCreate] =
    (
      InputValidators.validateEmail(userCreate.email),
      InputValidators.validatePassword(userCreate.password),
      InputValidators.validateFirstName(userCreate.firstName),
      InputValidators.validateLastName(userCreate.lastName),
      InputValidators.validateRole(userCreate.lastName)
    ).mapN(UserCreate)

}

object ObjectsValidators extends ObjectsValidators
