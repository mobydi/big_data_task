package com.scalaProj.kafkaUtils

import java.util.Properties

import com.typesafe.config.ConfigFactory
import kafka.admin.AdminUtils
import org.I0Itec.zkclient.ZkClient

case class TopicAdmin(zkClient: ZkClient) {

  def createTopic(name: String, partitionNum: Int = 1, replicationFactor: Int = 1, config: Properties = new Properties()): Unit = {
    AdminUtils.createTopic(zkClient, name, partitionNum, replicationFactor, config)
  }

  def topicExists(topicName: String) : Boolean = {
    AdminUtils.topicExists(zkClient, topicName)
  }
}

object TopicAdmin {
  def getConfigTopicName: String = {
    val config = ConfigFactory.load()
    config.getString("kafka.topic")
  }
}
