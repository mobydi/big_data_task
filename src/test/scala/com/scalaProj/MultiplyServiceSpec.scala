package com.scalaProj

import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{FreeSpec, Matchers}
import spray.http.StatusCodes._
import spray.testkit.ScalatestRouteTest

class MultiplyServiceSpec extends FreeSpec with SprayApiService with ScalatestRouteTest with Matchers {
  def actorRefFactory = system

  "Multiplication Service" - {
    "when calling GET /api/MultiplyService/40/20" - {
      "should return '800'" in {
        Get("/api/MultiplyService/40/20") ~> sprayApiRoute ~> check {
          Eventually.eventually(Eventually.timeout(Eventually.scaled(Span(5, Seconds))))(status should equal(OK))
          entity.toString should include("800")
        }
      }
    }
  }
}
