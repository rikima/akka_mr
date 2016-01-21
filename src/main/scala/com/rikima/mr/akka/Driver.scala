package com.rikima.mr.akka

import akka.actor.{Props, ActorSystem}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.ClusterDomainEvent
import akka.routing.ConsistentHashingGroup

/**
  * Created by mrikitoku on 2016/01/20.
  */
object Driver {
  def start() = {
    val system = ActorSystem("system")

    val num_mappers = 4
    val num_reducers = 4

    val master = system.actorOf(Master.props(num_mappers, num_reducers), name = "master")

    Cluster(system).subscribe(master, classOf[ClusterDomainEvent])
  }

  def main(args: String): Unit = {
    start
  }
}
