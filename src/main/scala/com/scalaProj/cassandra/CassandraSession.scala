package com.scalaProj.cassandra

object CassandraSession {
  val session = Helper.createSessionAndInitKeyspace()
}
