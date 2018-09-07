package com.scalaProj.consumer

import java.util.concurrent.ConcurrentHashMap

object ElapsedTimeSingletone {
  val map : ConcurrentHashMap[String, Double] = new ConcurrentHashMap[String, Double]
}
