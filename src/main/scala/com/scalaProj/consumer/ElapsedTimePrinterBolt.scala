package com.scalaProj.consumer

import backtype.storm.tuple.Tuple
import com.scalaProj.storm.StormBolt
import play.api.libs.json.Json

class ElapsedTimePrinterBolt extends StormBolt(outputFields = List("word")) {
  def execute(t: Tuple) {
    implicit val reads = Json.reads[Message]
    val json = t.getString(0)
    val message = Json.parse(json).as[Message]
    ElapsedTimeSingletone.map.put(message.opType,
      ElapsedTimeSingletone.map.getOrDefault(message.opType, 0) + message.elapsedMs)

    println("+++++++=====================[Elapsed for " + message.opType +
      " operation: " + ElapsedTimeSingletone.map.get(message.opType) + " ms so far this session]==============+++++++")
    t emit json
    t ack
  }
}