package com.scalaProj.kafkaUtils

import java.util.Properties

import kafka.producer.{Producer => KafkaProducer}
import kafka.utils.ZKStringSerializer
import org.I0Itec.zkclient.ZkClient
import org.I0Itec.zkclient.serialize.ZkSerializer

object ZookeeperUtils {

  def createClient(
    config: Properties = KafkaConfig(),
    sessTimeout: Int = 10000,
    connTimeout: Int = 10000,
    serializer: ZkSerializer = ZKStringSerializer): ZkClient = {
    val host = config.getProperty("zookeeper.connect")
    new ZkClient(host, sessTimeout, connTimeout, serializer)
  }

  def createTopicIfNotExists(name: String): Unit = {
    val zookeeperClient = createClient()
    val topicAdmin = TopicAdmin(zookeeperClient)
    if (!topicAdmin.topicExists(name)) {
      topicAdmin.createTopic(name)
    }
  }
}
