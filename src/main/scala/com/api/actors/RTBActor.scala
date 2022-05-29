package com.api.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.api.data.Bid.{BidRequest, BidResponseWithScore}
import com.api.data.Campaign.Campaign

object RTBActor {
  case class MakeABidRequest(request: BidRequest, replyTo: ActorRef[Option[BidResponseWithScore]])

  sealed trait BidStatus
  final case class  BidRequestAccepted(request: BidRequest) extends BidStatus
  final case object BidRequestRejected extends BidStatus

  def apply(campaign: Campaign): Behavior[MakeABidRequest] = Behaviors.receiveMessage {
    message =>
      val bannerScoreOpt = message.request.impressionsOpt.map(campaign.bannerScoreViaImpressions)
      val userAndGeoScoreOpt = message.request.userOpt.map(campaign.userAndGeoScore)

      Behaviors.same
  }
}
