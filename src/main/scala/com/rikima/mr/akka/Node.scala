package com.rikima.mr.akka

import akka.actor.{Props, ActorSystem}


/**
  * Created by mrikitoku on 2016/01/20.
  */
class Node {

  def start (host: String, port: String, num_mappers: Int = 2, num_reducers: Int = 2): Unit = {
    System.setProperty("akka.remote.netty.tcp.hostname", host)
    System.setProperty("akka.remote.netty.tcp.port", port)

    val system = ActorSystem("system")

    // generate mapper
    (1 to num_mappers).foreach {
      case id => system.actorOf(Props[Mapper], name=s"m$id")
    }

    // generate reducers
    (1 to num_reducers).foreach {
      case id => system.actorOf(Props[Reducer], name=s"r$id")
    }
  }

  def main(args: Array[String]): Unit = {
    var host = ""
    var port = ""
    for (i <- 0 until args.size) {
      val a = args(i)
      if (a == "-h" || a == "--host") {
        host = args(i+1)
      }
      if (a == "-p" || a == "--port") {
        port = args(i+1)
      }
    }

    if (host.isEmpty || port.isEmpty) {
      println(s"com.rikima.mr.akk.Node -h [host] -p [port]")
      System.exit(1)
    }

    start(host, port)
  }
}
