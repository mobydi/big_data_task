package com.scalaProj.cassandra

import com.datastax.driver.core._
import com.typesafe.config.ConfigFactory

object Helper {

  def createSessionAndInitKeyspace(uri: CassandraConnectionUri,
                                   defaultConsistencyLevel: ConsistencyLevel = QueryOptions.DEFAULT_CONSISTENCY_LEVEL) = {
    val cluster = new Cluster.Builder().
      addContactPoints(uri.hosts.toArray: _*).
      withPort(uri.port).
      withQueryOptions(new QueryOptions().setConsistencyLevel(defaultConsistencyLevel)).build

    val session = cluster.connect
    session.execute(s"USE ${uri.keyspace}")
    session
  }

  def createSessionAndInitKeyspace() : Session = {
    val config = ConfigFactory.load()
    val uri = CassandraConnectionUri("cassandra://"+config.getString("cassandra.host")+":"+config.getInt("cassandra.port")+"/"+config.getString("cassandra.keyspace"))
    val session = createSessionAndInitKeyspace(uri)
    session
  }


}