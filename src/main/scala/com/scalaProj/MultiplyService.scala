package com.scalaProj

import java.util

import _root_.play.api.libs.json.Json
import akka.actor.Actor
import akka.event.Logging
import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.scalaProj.cassandra.Helper
import com.scalaProj.enums.OperationType.OperationType
import com.scalaProj.enums._
import com.scalaProj.kafkaUtils.TopicAdmin._
import com.scalaProj.pojo.HistoryRow
import com.scalaProj.producer.Producer
import spray.routing.RequestContext

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

object MultiplyService {

  case class Process(op1: Double, op2: Double)

  case class Results(count: Integer)

}

object SessionContainer {
  val session = Helper.createSessionAndInitKeyspace()
}

class MultiplyService(requestContext: RequestContext) extends Actor {

  object Check

  import MultiplyService._

  val session = SessionContainer.session

  implicit val system = context.system
  val log = Logging(system, getClass)

  val strProducer = Producer[String](getConfigTopicName)

  override def receive = {
    case Process(op1, op2) =>
      process(op1, op2)
      context.stop(self)
    case Results(count) =>
      results(count)
      context.stop(self)
  }

  def sendMetricsMessage(str: String, elapsedMs: Double, opType: OperationType): Unit = {
    val message = Message(str, elapsedMs, opType toString)
    implicit val writes = Json.writes[Message]
    val kafkaMessageSending = new Thread(new Runnable {
      override def run(): Unit = {
        strProducer.send(Json.toJson(message) toString)
      }
    })
    kafkaMessageSending.start()
  }

  def process(op1: Double, op2: Double) = {
    log.info("Requesting multiply operation operand1: {}, operand2: {}", op1, op2)
    val startTime = System.nanoTime()
    val result = op1 * op2
    saveResult(op1, op2, result)
    val finishTime = System.nanoTime()
    sendMetricsMessage("Operation: " + op1 + "*" + op2 + "=" + result, (finishTime - startTime) / 1000000, OperationType.MULTIPLICATION)
    requestContext.complete(result toString)
  }

  def results(count: Integer) = {
    log.info("Requesting last {} results for multiplication", count)
    val startTime = System.nanoTime()
    val selectStmt = select().column("op1").column("op2").column("result")
      .from("results")
      .limit(count)
    val resultSet = session.execute(selectStmt)
    var historyRows = new ListBuffer[HistoryRow]
    val iter: util.Iterator[Row] = resultSet.iterator()
    while (iter.hasNext) {
      val row: Row = iter.next()
      historyRows += new HistoryRow(row.getDouble("op1"), row.getDouble("op2"), row.getDouble("result"))
    }
    val finishTime = System.nanoTime()
    sendMetricsMessage("Retrieving history with limit: " + count, (finishTime - startTime) / 1000000, OperationType.HISTORY)
    requestContext.complete(historyRows toString())
  }

  def saveResult(op1: Double, op2: Double, result: Double) = {
    session.execute("CREATE TABLE IF NOT EXISTS results (uid uuid primary key, op1 double, op2 double, result double, processed timestamp)")
    session.execute("INSERT INTO results (uid, op1, op2, result, processed) " +
      "VALUES " +
      "(blobAsUuid(timeuuidAsBlob(now())), " + op1 + ", " + op2 + ", " + result + ",dateOf(now()));")
  }

  case class Message(str: String, elapsedMs: Double, opType: String)

}
