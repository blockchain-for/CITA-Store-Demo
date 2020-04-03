package com.slashchain.cita

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.slashchain.JsonFormats
import com.slashchain.cita.CitaRegistry._
import com.slashchain.config.AppConfig

import scala.concurrent.Future


class CitaRoutes(citaRegistry: ActorRef[CitaRegistry.Command])(implicit val system: ActorSystem[_]) {

  //#user-routes-class
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._
  //#import-json-formats

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout = Timeout(AppConfig.settings.app.routes.askTimeout)

  def createStore(content: StoreContent): Future[CitaActionPerformed] = citaRegistry.ask(CreateStore(content, _))

  def getStore(hash: String): Future[GetStoreResponse] = citaRegistry.ask(GetStore(hash, _))

  // cita-url
  val citaRoutes: Route =
    pathPrefix("cita") {
      concat(
        post {
          entity(as[StoreContent]) { content =>
            onSuccess(createStore(content)) { performed =>
              complete((StatusCodes.Created, performed))

            }
          }
        },
        path(Segment) { hash =>
          get {
            //#retrieve-user-info
            rejectEmptyResponse {
              onSuccess(getStore(hash)) { response =>
                complete((StatusCodes.OK, response))
              }
            }
            //#retrieve-user-info
          }
        })
    }
}
