package com.rikima.mr.akka

import akka.actor.{Props, Actor, ActorLogging}
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, UnreachableMember, MemberUp}

/**
  * Created by mrikitoku on 2016/01/20.
  */
case class MapperResponse(data: String)
case class MapperRequest(data: String)

object Mappers {
  def props(start_id: Int, num: Int): Props = Props(new Mappers(start_id, num))
}

class Mappers(start_id: Int, num: Int = 2) extends Actor {
  (0 until num).foreach {
    case c =>
      val id = start_id + c
      val a = context.actorOf(Props[Mapper], name=s"m$id")
      println(s"mapper actor: $a")
  }

  def receive = {
    case message =>
      println(message)
  }
}

class Mapper extends Actor with ActorLogging {


  override def preStart = {
    log.debug(Thread.currentThread.getName + " is started.")
  }


  override def postStop = {
    log.debug(Thread.currentThread.getName + " is stopped.")
  }


  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)

    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)

    case MapperRequest(data) =>
      log.info(s"MapperRequest($data)")
      sender ! MapperResponse(data)

    case _: MemberEvent => // ignore
  }
}
