package com.slashchain.config

import cats.effect.IO

import io.circe.generic.auto._
import io.circe.config.syntax._
import io.circe.config.parser

import scala.concurrent.duration.FiniteDuration

case class AppConfig(routes: RouteConfig, cita: CitaConfig )

case class AppSettings(app: AppConfig)

object AppConfig {
  lazy val settings: AppSettings = {
    parser.decodeF[IO, AppSettings]().unsafeRunSync()
  }



}

case class RouteConfig(askTimeout: FiniteDuration)