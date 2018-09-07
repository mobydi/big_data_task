package com.scalaProj

import akka.actor.{Actor, Props}
import spray.routing._

class SprayApiServiceActor extends Actor with SprayApiService {

  def actorRefFactory = context

  def receive = runRoute(sprayApiRoute)
}

trait SprayApiService extends HttpService {
  val sprayApiRoute =
    pathPrefix("api") {
      path("MultiplyService" / DoubleNumber / DoubleNumber) { (op1, op2) =>
        requestContext =>
          val multiplyService = actorRefFactory.actorOf(Props(new MultiplyService(requestContext)))
          multiplyService ! MultiplyService.Process(op1, op2)
      } ~
        path("MultiplyService" / "history" / IntNumber) { (count) =>
          requestContext =>
            val multiplyService = actorRefFactory.actorOf(Props(new MultiplyService(requestContext)))
            multiplyService ! MultiplyService.Results(count)
        }
    }
}
