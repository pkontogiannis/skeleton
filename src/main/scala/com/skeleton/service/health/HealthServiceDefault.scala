package com.skeleton.service.health

import java.lang.management.ManagementFactory

import com.skeleton.service.health.HealthModel.HealthStatus
import com.zaxxer.hikari.HikariPoolMXBean
import javax.management.{ JMX, ObjectName }

class HealthServiceDefault extends HealthService {
  private lazy val poolProxy = JMX.newMXBeanProxy(
    ManagementFactory.getPlatformMBeanServer,
    new ObjectName("com.zaxxer.hikari:type=Pool (database)"),
    classOf[HikariPoolMXBean]
  )

  def live: HealthStatus = HealthStatus.OK

  def ready: HealthStatus = if (poolProxy.getTotalConnections > 0) HealthStatus.OK else HealthStatus.INIT

}
