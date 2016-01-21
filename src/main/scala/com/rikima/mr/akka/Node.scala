package com.rikima.mr.akka

import akka.actor.ActorSystem

/**
  * Created by mrikitoku on 2016/01/20.
  */
object Node {

  def start (host: String, port: String, start_id: Int = 1, num_mappers: Int = 2, num_reducers: Int = 2): Unit = {
    System.setProperty("akka.remote.netty.tcp.hostname", host)
    System.setProperty("akka.remote.netty.tcp.port", port)

    val system = ActorSystem("system")

    system.actorOf(Mappers.props(start_id, num_mappers), name="mappers")
    system.actorOf(Reducers.props(start_id, num_reducers), name="reducers")
  }

  def main(args: Array[String]): Unit = {
    var host = ""
    var port = ""
    var num_mappers = 2
    var num_reducers = 2
    var start_id = 1
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
    }

    if (host.isEmpty || port.isEmpty) {
      println(s"com.rikima.mr.akk.Node -h [host] -p [port]")
      System.exit(1)
    }

    start(host, port, start_id, num_mappers, num_reducers)
  }
}
