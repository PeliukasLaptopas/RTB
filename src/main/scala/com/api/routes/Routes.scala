package com.api.routes


import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.api.actors.RTBActor
import com.api.json.JsonSupport

class Routes(data: ActorRef[RTBActor.MakeABidRequest])(implicit val system: ActorSystem[_]) extends JsonSupport {

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  private val actorMaterializer = ActorMaterializer

  val routes: Route = ???
}
