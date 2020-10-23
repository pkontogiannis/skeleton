package com.skeleton.utils.validators

import cats.data.ValidatedNec
import cats.implicits._
import com.skeleton.service.errors._

/*
 * Check the link bellow for more info
 * @link https://typelevel.org/cats/datatypes/validated.html
 * */

sealed trait InputValidators {

  type ValidationResult[A] = ValidatedNec[DomainValidation, A]

  def validateEmail(email: String): ValidationResult[String] =
    if (email.matches(
          """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"""
        )) email.validNec
    else EmailHasSpecialCharacters.invalidNec

  def validatePassword(password: String): ValidationResult[String] =
    if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) password.validNec
    else PasswordDoesNotMeetCriteria.invalidNec

  def validateFirstName(firstName: String): ValidationResult[String] =
    if (firstName.matches("^[a-zA-Z]+$")) firstName.validNec else FirstNameHasSpecialCharacters.invalidNec

  def validateLastName(lastName: String): ValidationResult[String] =
    if (lastName.matches("^[a-zA-Z]+$")) lastName.validNec else LastNameHasSpecialCharacters.invalidNec

  def validateRole(lastName: String): ValidationResult[String] =
    if (lastName.matches("^[a-zA-Z]+$")) lastName.validNec else LastNameHasSpecialCharacters.invalidNec

}

object InputValidators extends InputValidators
