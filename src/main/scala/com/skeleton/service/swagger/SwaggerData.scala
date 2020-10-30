package com.skeleton.service.swagger

import com.skeleton.service.user.UserModel.UserCreate

object SwaggerData {

  final val exampleUser1: UserCreate = UserCreate(
    email     = "petros@gmail.com",
    firstName = "Petros",
    lastName  = "Kon",
    password  = "Passw0rd",
    role      = "admin"
  )
  final val exampleUser2: UserCreate = UserCreate(
    email     = "manos@gmail.com",
    firstName = "Manos",
    lastName  = "Pal",
    password  = "Passw0rd",
    role      = "admin"
  )

  final val userName1       = "Petros"
  final val createUser1Json =
    //    exampleUser1.asJson.toString()
    "{\"email\":\"petros@gmail.com\",\"firstName\":\"Petros\",\"lastName\":\"Kon\",\"password\":\"passw0rd\",\"role\":\"admin\"}"
  final val user1LoginJSON =
    "{\"email\":\"petros@gmail.com\",\"password\":\"passw0rd\"}"
  final val userDto1JSON =
    "{\"userId\":\"743278a1-3dd4-49fe-bb00-49b63caf3a5b\",\"email\":\"petros@gmail.com\",\"firstName\":\"Petros\",\"lastName\":\"Kon\",\"role\":\"admin\"}"
  final val userLoginDto1JSON =
    "{ \"email\": \"petros@gmail.com\", \"accessToken\": { \"token\": \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsInN1YiI6ImMzOWNhOWQxLTIwMTAtNDQxMC1hZGY1LWE0ZjI1NTY2OWE2MSIsImV4cCI6MTU4NTkyMjY5NCwiaWF0IjoxNTg1OTE5MDk0fQ.aizjg7LYGwqbloAHrDeuH7l0fcRc4g-KtI2foCQYok8\", \"expiresIn\": 3600 }, \"refreshToken\": { \"token\": \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsInN1YiI6ImMzOWNhOWQxLTIwMTAtNDQxMC1hZGY1LWE0ZjI1NTY2OWE2MSIsImV4cCI6MTU4NjAwNTQ5NCwiaWF0IjoxNTg1OTE5MDk0fQ.HwLlvfeDA_9jdfof-L73J4SW_lL9eMVvKPeU_ptwqNg\", \"expiresIn\": 86400 }, \"role\": \"admin\", \"tokenType\": \"Bearer\" }"
  final val user1Token =
    "{ \"token\": \"Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsInN1YiI6ImMzOWNhOWQxLTIwMTAtNDQxMC1hZGY1LWE0ZjI1NTY2OWE2MSIsImV4cCI6MTU4NTkyMzA0OSwiaWF0IjoxNTg1OTE5NDQ5fQ.dpxrkKzhpHSqvNuXBq_kTjSwEYucQePYsNGqLxnyZ_Y\", \"expiresIn\": 3600 }"

  final val userName2 = "Manos"
  final val createUser2JSON =
    "{\"email\":\"manos@gmail.com\",\"firstName\":\"Manos\",\"lastName\":\"Pal\",\"password\":\"passw0rd\",\"role\":\"admin\"}"
  final val user2LoginJSON =
    "{\"email\":\"manos@gmail.com\",\"password\":\"passw0rd\"}"
  final val userDto2JSON =
    "{\"userId\":\"743278a1-3dd4-49fe-bb00-49b63caf3a5b\",\"email\":\"manos@gmail.com\",\"firstName\":\"Manos\",\"lastName\":\"Pal\",\"role\":\"admin\"}"
  final val userLoginDto2JSON =
    "{\"email\":\"manos@gmail.com\",\"accessToken\":{\"token\":\"BearereyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsInN1YiI6ImMzOWNhOWQxLTIwMTAtNDQxMC1hZGY1LWE0ZjI1NTY2OWE2MSIsImV4cCI6MTU4NTkyMjY5NCwiaWF0IjoxNTg1OTE5MDk0fQ.aizjg7LYGwqbloAHrDeuH7l0fcRc4g-KtI2foCQYok8\",\"expiresIn\":3600},\"refreshToken\":{\"token\":\"BearereyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsInN1YiI6ImMzOWNhOWQxLTIwMTAtNDQxMC1hZGY1LWE0ZjI1NTY2OWE2MSIsImV4cCI6MTU4NjAwNTQ5NCwiaWF0IjoxNTg1OTE5MDk0fQ.HwLlvfeDA_9jdfof-L73J4SW_lL9eMVvKPeU_ptwqNg\",\"expiresIn\":86400},\"role\":\"admin\",\"tokenType\":\"Bearer\"}"
  final val user2Token =
    "{\"token\":\"BearereyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhZG1pbiIsInN1YiI6ImMzOWNhOWQxLTIwMTAtNDQxMC1hZGY1LWE0ZjI1NTY2OWE2MSIsImV4cCI6MTU4NTkyMzA0OSwiaWF0IjoxNTg1OTE5NDQ5fQ.dpxrkKzhpHSqvNuXBq_kTjSwEYucQePYsNGqLxnyZ_Y\",\"expiresIn\":3600}"

  final val listOfUsers =
    "[ { \"userId\": \"c39ca9d1-2010-4410-adf5-a4f255669a61\", \"email\": \"pkontogiannis4@gmail.com\", \"firstName\": \"Petros\", \"lastName\": \"Kontogiannis\", \"role\": \"admin\" }, { \"userId\": \"743278a1-3dd4-49fe-bb00-49b63caf3a5b\", \"email\": \"petros@gmail.com\", \"firstName\": \"Manos\", \"lastName\": \"Palavras\", \"role\": \"admin\" }, { \"userId\": \"694638df-9933-4a17-b080-171e457e9b3f\", \"email\": \"pkontogiannis24@gmail.com\", \"firstName\": \"Petros\", \"lastName\": \"Kontogiannis\", \"role\": \"admin\" } ]"

  final val recordAlreadyExists = "{\"code\":\"RecordAlreadyExists\",\"message\":\"This email already exists\"}"
  final val unauthorized        = "{\"code\":\"Unauthorized\",\"message\":\"Unauthorized Operation\"}"
  final val internalError       = "{\"code\":\"internalError\",\"message\":\"Internal Server Error\"}"
  final val notFound            = "{\"code\":\"DefaultNotFoundError\",\"message\":\"Can't find requested asset\"}"
}
