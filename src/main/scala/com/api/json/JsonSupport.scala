package com.api.json


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.api.data.Bid.BidResponse
import com.api.data.Campaign.Banner
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val bannerM: RootJsonFormat[Banner] = jsonFormat4(Banner)
  implicit val bidResponseM: RootJsonFormat[BidResponse] = jsonFormat5(BidResponse)
}
