package com.scalaProj.consumer

import com.scalaProj.kafkaUtils.KafkaConfig
import kafka.consumer._

abstract class Consumer(topics: List[String]) {

  protected val kafkaConfig = KafkaConfig()
  protected val config = new ConsumerConfig(kafkaConfig)

  def read(): Iterable[String]
}