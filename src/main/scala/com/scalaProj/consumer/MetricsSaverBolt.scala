package com.scalaProj.consumer

import java.util

import backtype.storm.task.{OutputCollector, TopologyContext}
import backtype.storm.tuple.Tuple
import com.datastax.driver.core.Session
import com.scalaProj.cassandra.CassandraSession
import com.scalaProj.storm.StormBolt
import play.api.libs.json.Json

class MetricsSaverBolt extends StormBolt(outputFields = List("word")) {
  var session: Session = null

  def execute(t: Tuple) {
    implicit val reads = Json.reads[Message]
    val json = t.getString(0)
    val message = Json.parse(json).as[Message]
    saveMetrics(message)
    t emit json
    println(json)
    t ack
  }

  override def prepare(conf: util.Map[_, _], context: TopologyContext, collector: OutputCollector) {
    session = CassandraSession.session
    super.prepare(conf, context, collector)
  }

  def saveMetrics(message: Message) = {
    session.execute("CREATE TABLE IF NOT EXISTS metrics (uid uuid primary key, operation_text text, operation_type text, elapsed_ms double, log_date timestamp)")
    session.execute("INSERT INTO metrics (uid, operation_text, operation_type, elapsed_ms, log_date) " +
      "VALUES " +
      "(blobAsUuid(timeuuidAsBlob(now())), '" + message.str + "', '" + message.opType + "', " + message.elapsedMs + ", dateOf(now()));")
  }
}