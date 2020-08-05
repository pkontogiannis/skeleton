package com.skeleton.utils.oauth2

case class VerifiedToken(token: String, id: String, name: String, username: String, email: String, roles: Seq[String])
