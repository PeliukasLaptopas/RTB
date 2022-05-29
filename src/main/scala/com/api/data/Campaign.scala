package com.api.data

import cats.data.NonEmptyList
import com.api.data.Bid.{Gender, Impression, Site, User}

object Campaign {
  /*
  -Depending on which metric is more important we can represent it by an integer -
  if we later want to adjust metric scores we can simple take or add one more zero
  to that number and it will either prioritize it more or less.
  -If we think that two metrics should be valued the same, then we can just use the same values.
  */
  val EMPTY_SCORE = 0

  val AGE_SCORE_10015 = 10015
  val AGE_SCORE_10010 = 10010
  val AGE_SCORE_10005 = 10005

  val BANNER_SCORE_10015 = 10015
  val BANNER_SCORE_10010 = 10010
  val BANNER_SCORE_10005 = 10005

  //device.geo has higher priority than user.geo
  val COUNTRY_SCORE_1010 = 1010
  val DEVICE_SCORE_1015 = 1015

  val GENDER_SCORE_1010 = 10010
  val GENDER_SCORE_1000 = 10000

  val SITE_SCORE_10010 = 10010

  sealed trait BannerMetric
  case class BannerScore(score: Int, bestBanner: Banner) extends BannerMetric
  case object NoValidBannerFound extends BannerMetric

  case class UserAndGeoScore(score: Int)
  case class SiteScore(score: Int)

  case class Campaign(id: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double) {
    def enoughMoneyForBid(min: Double): Boolean = min <= bid

    def siteScore(site: Site): SiteScore = {
      if (targeting.targetedSiteIds.contains(site.id))
        SiteScore(SITE_SCORE_10010)
      else
        SiteScore(EMPTY_SCORE)
    }

    def userAndGeoScore(user: User): UserAndGeoScore = {
      val minAgePass = user.minAgeOpt.flatMap(gotMinAge => Some(gotMinAge < targeting.minAge))
      val maxAgePass = user.minAgeOpt.flatMap(gotMinAge => Some(gotMinAge < targeting.minAge))

      val ageScore = (minAgePass, maxAgePass) match {
        case (Some(true), Some(true)) => AGE_SCORE_10015
        case (Some(true), None) => AGE_SCORE_10010
        case (None, Some(true)) => AGE_SCORE_10010
        case _ => EMPTY_SCORE
      }

      val sameCountriesPassOpt = for {
        geo <- user.geoOpt
        country <- geo.countryOpt
      } yield country == targeting.country

      val countryScore = sameCountriesPassOpt.fold(EMPTY_SCORE) {
        case true  => COUNTRY_SCORE_1010
        case false => EMPTY_SCORE
      }

      val genderPassOpt = user.genderOpt.flatMap(gotGender => Some(gotGender == targeting.gender))

      val genderScore = genderPassOpt.fold(EMPTY_SCORE) {
        case true  => GENDER_SCORE_1010
        case false => EMPTY_SCORE
      }

      UserAndGeoScore(ageScore + countryScore + genderScore)
    }

    def bannerScoreViaImpressions(impressions: List[Impression]): BannerMetric = {
      val filteredBanners = impressions.map(bannerScoreViaImpression).collect[BannerScore] {
        case banner@BannerScore(_, _) => banner
      }

      filteredBanners.sortBy(_.score) match {
        case h :: _ => h
        case _ => NoValidBannerFound
      }
    }

    //todo if BannerScore has empty, redo with what we have left
    private def bannerScoreViaImpression(imp: Impression): BannerMetric = {
      (imp.wOpt, imp.hOpt, imp.wMinOpt, imp.hMinOpt, imp.wMaxOpt, imp.hMaxOpt) match {
        case (Some(w), Some(h), _, _, _, _) => BannerScore(BANNER_SCORE_10015, bannersThatFits(w, h).head)

        case (Some(w), None, _, Some(hMin), None, None) => BannerScore(BANNER_SCORE_10010, bannersThatFits(w, hMin).head)
        case (Some(w), None, _, None, None, Some(hMax)) => BannerScore(BANNER_SCORE_10010, bannersThatFits(w, hMax).head)

        case (None, Some(h), Some(wMin), _, None, None) => BannerScore(BANNER_SCORE_10010, bannersThatFits(wMin, h).head)
        case (None, Some(h), None, None, Some(wMax), None) => BannerScore(BANNER_SCORE_10010, bannersThatFits(wMax, h).head)

        case (None, None, Some(wMin), None, None, Some(hMax)) => BannerScore(BANNER_SCORE_10005, bannersThatFits(wMin, hMax).head)
        case (None, None, Some(wMin), Some(hMin), None, None) => BannerScore(BANNER_SCORE_10005, bannersThatFits(wMin, hMin).head)

        case (None, None, Some(wMin), Some(hMin), None, None) => BannerScore(BANNER_SCORE_10005, bannersThatFits(wMin, hMin).head)
        case (None, None, None, Some(hMin), Some(wMax), None) => BannerScore(BANNER_SCORE_10005, bannersThatFits(wMax, hMin).head)
      }
    }

    private def bannersThatFits(w: Int, h: Int): List[Banner] = {
      banners.filter(b => w <= b.width && h <= b.height)
    }
  }

  case class Targeting(targetedSiteIds: Seq[String], country: String, gender: Gender, minAge: Int, maxAge: Int)
  case class Banner(id: Int, src: String, width: Int, height: Int)
}
