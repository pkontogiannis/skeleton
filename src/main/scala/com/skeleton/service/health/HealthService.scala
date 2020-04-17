package com.skeleton.service.health

import com.skeleton.service.health.HealthModel.HealthStatus
import com.typesafe.scalalogging.LazyLogging

trait HealthService extends LazyLogging {

  def live: HealthStatus

  def ready: HealthStatus

}
