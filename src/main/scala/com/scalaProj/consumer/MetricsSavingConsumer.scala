package com.scalaProj.consumer

import java.util.Random

import _root_.backtype.storm.topology.TopologyBuilder
import backtype.storm.spout.SchemeAsMultiScheme
import backtype.storm.{Config, LocalCluster}
import com.scalaProj.kafkaUtils.TopicAdmin._
import com.scalaProj.kafkaUtils.ZookeeperUtils._
import com.typesafe.config.ConfigFactory
import storm.kafka._

object MetricsSavingConsumer {
  def main(args: Array[String]): Unit = {
    val topicName =
      if (args.length == 0) getConfigTopicName
      else args(0)

    createTopicIfNotExists(topicName)

    val builder: TopologyBuilder = new TopologyBuilder()

    val kafkaSpout = createKafkaSpout(topicName)
    builder.setSpout("kafka", kafkaSpout, 1)
    builder.setBolt("metricsSaver", new MetricsSaverBolt, 3)
      .shuffleGrouping("kafka")
    builder.setBolt("elapsedTime", new ElapsedTimePrinterBolt, 4)
      .shuffleGrouping("metricsSaver")

    val conf = new Config()
    conf setDebug true

    val cluster = new LocalCluster()
    try {
      cluster.submitTopology("test", conf, builder.createTopology())
      scala.io.StdIn.readLine()
    } finally {
      cluster.killTopology("test")
      println("Shutting down cluster...")
      cluster.shutdown()
    }
  }

  def createKafkaSpout(topicName: String): KafkaSpout = {
    val config = ConfigFactory.load()
    val zookeepers = config.getString("consumer.zookeeper.connect")
    val hosts = new ZkHosts(zookeepers)
    val spoutConfig = new SpoutConfig(hosts, topicName, "", "id" + new Random().nextLong())
    spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme())
    val kafkaSpout = new KafkaSpout(spoutConfig)
    kafkaSpout
  }
}

case class Message(str: String, elapsedMs: Double, opType: String)