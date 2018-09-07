package com.scalaProj.producer

import java.util.Properties

import com.scalaProj.kafkaUtils.TopicAdmin._
import com.scalaProj.kafkaUtils.ZookeeperUtils._
import com.scalaProj.kafkaUtils.{TopicAdmin, KafkaConfig}
import kafka.producer.{KeyedMessage, Producer => KafkaProducer, ProducerConfig}

case class Producer[A](topic: String) {
  protected val config = new ProducerConfig(KafkaConfig())
  private lazy val producer = new KafkaProducer[A, A](config)

  def send(message: A) = sendMessage(producer, keyedMessage(topic, message))

  def sendStream(stream: Stream[A]) = {
    val iter = stream.iterator
    while(iter.hasNext) {
      send(iter.next())
    }
  }

  private def keyedMessage(topic: String, message: A): KeyedMessage[A, A] = new KeyedMessage[A, A](topic, message)
  private def sendMessage(producer: KafkaProducer[A, A], message: KeyedMessage[A, A]) = producer.send(message)
}


object Producer {
  def apply[T](topic: String, props: Properties) = new Producer[T](topic) {
    createTopicIfNotExists(getConfigTopicName)
    override val config = new ProducerConfig(props)

  }
}