package com.rikima.mr.akka

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.ClusterDomainEvent
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.{FromConfig, ConsistentHashingGroup}

/**
  * Created by mrikitoku on 2016/01/20.
  */
object Driver {
  def process(master: ActorRef): Unit = {
    val dataset = (1 to 10).foreach {
      case d => {
        master ! MapperRequest(d.toString)
      }
    }
  }

  def start (hosts: String, host: String, port: String, start_id: Int, num_mappers: Int = 2, num_reducers: Int = 2): Unit = {
    System.setProperty("akka.remote.netty.tcp.hostname", host)
    System.setProperty("akka.remote.netty.tcp.port", port)

    val system = ActorSystem("system")
    val master = system.actorOf(Master.props(hosts, num_mappers, num_reducers), name = "master")

    Cluster(system).subscribe(master, classOf[ClusterDomainEvent])

    system.actorOf(Mappers.props(start_id, num_mappers), name="mappers")
    system.actorOf(Reducers.props(start_id, num_reducers), name="reducers")

    Thread.sleep(10 * 1000)

    // sample
    process(master)
  }


  def main(args: Array[String]): Unit = {
    var host = ""
    var port = ""
    var num_mappers = 2
    var num_reducers = 2
    var start_id = 1
    var hosts = ""
    for (i <- 0 until args.size) {
      val a = args(i)
      if (a == "-h" || a == "--host") {
        host = args(i+1)
      }
      if (a == "-p" || a == "--port") {
        port = args(i+1)
      }
      if (a == "-nm" || a == "--num_mappers") {
        num_mappers = args(i+1).toInt
      }
      if (a == "-nr" || a == "--num_reducers") {
        num_reducers = args(i+1).toInt
      }
      if (a == "-s" || a == "--start_id") {
        start_id = args(i+1).toInt
      }
      if (a == "-hs" || a == "--hosts") {
        hosts = args(i+1)
      }
    }

    if (host.isEmpty || port.isEmpty || hosts.isEmpty) {
      println(s"com.rikima.mr.akk.Driver -h [host] -p [port] -hs [host1, host2,...]")
      System.exit(1)
    }

    start(hosts, host, port, start_id, num_mappers, num_reducers)
  }
}
