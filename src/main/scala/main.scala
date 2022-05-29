import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import akka.util.Timeout
import com.api.actors.RTBActor
import com.api.data.Bid.{BidRequest, Male, Site}
import com.api.data.Campaign.{Banner, Campaign, Targeting}
import com.api.routes.Routes
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Failure
import scala.util.Success

object main {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  //#start-http-server
  def main(args: Array[String]): Unit = {
    val activeCampaign = Seq(
      Campaign(
        id = 1,
        country = "LT",
        targeting = Targeting(targetedSiteIds = Seq("0006a522ce0f4bbbbaa6b3c38cafaa0f"), "foo", Male, 16, 50),
        banners = List(Banner(id = 1,
          src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg", width = 300, height = 250)), bid = 5d
      )
    )

    val root = Behaviors.setup[Nothing] { context =>
      val campaigns = activeCampaign.map(ac => {
        val rtbActor = context.spawn(RTBActor(ac), "RTBActor" + ac.id)

        val timeout = Timeout.create(context.system.settings.config.getDuration("rtb.routes.ask-timeout"))

       rtbActor.ask(RTBActor.MakeABidRequest(BidRequest("IDDDDDD", None, Site("SITE ID", "DOMAIN"), None, None), _))(timeout, context.system.scheduler)
      })

      Behaviors.same
    }

    val system = ActorSystem[Nothing](root, "HelloAkkaHttpServer", config)
  }

  private def config: Config =
    ConfigFactory.parseString(s"""
       akka.persistence.journal.plugin = "akka.persistence.journal.inmem"
        """).withFallback(ConfigFactory.load())
}
