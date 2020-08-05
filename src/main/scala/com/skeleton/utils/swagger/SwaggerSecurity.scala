package com.skeleton.utils.swagger

import io.swagger.v3.oas.models.security.SecurityRequirement

trait SwaggerSecurity {

//  val bearerAuth: SecurityRequirement = new SecurityRequirement().addList("bearerAuth")
  val bearerAuth: SecurityRequirement = new SecurityRequirement().addList("oauth2")

}
