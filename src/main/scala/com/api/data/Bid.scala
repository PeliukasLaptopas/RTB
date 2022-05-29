package com.api.data

import com.api.data.Campaign.Banner

object Bid {
  trait Gender
  case object Male extends Gender
  case object Female extends Gender

  case class BidRequest(id: String, impressionsOpt: Option[List[Impression]], site: Site, userOpt: Option[User], deviceOpt: Option[Device])
  case class BidResponse(id: String, bidRequestId: String, price: Double, addIdOpt: Option[String], bannerOpt: Option[Banner])
  case class BidResponseWithScore(score: Int, bidResponse: BidResponse)

  case class Impression(id: String, wMinOpt: Option[Int], wMaxOpt: Option[Int], wOpt: Option[Int],
                        hMinOpt: Option[Int], hMaxOpt: Option[Int], hOpt: Option[Int], bidFloorOpt: Option[Double])
  case class Site(id: String, domain: String)
  case class User(id: String, minAgeOpt: Option[Int], maxAgeOpt: Option[Int], genderOpt: Option[Gender], geoOpt: Option[Geo])
  case class Device(id: String, geoOpt: Option[Geo])
  case class Geo(countryOpt: Option[String])
}
