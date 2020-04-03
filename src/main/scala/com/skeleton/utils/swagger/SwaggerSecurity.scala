package com.skeleton.utils.swagger

import io.swagger.v3.oas.models.security.SecurityRequirement

trait SwaggerSecurity {

  val bearerAuth = new SecurityRequirement().addList("bearerAuth")

}
