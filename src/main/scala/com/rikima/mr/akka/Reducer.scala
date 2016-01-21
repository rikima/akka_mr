package com.rikima.mr.akka

import akka.actor.{Actor, ActorLogging}
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, UnreachableMember, MemberUp}

/**
  * Created by mrikitoku on 2016/01/20.
  */

case class ReducerRequest(data: String)
case class ReducerResponse(data: String)

class Reducer extends Actor with ActorLogging {

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
    case MemberRemoved(member, previousStatus) => {
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    }
    case ReducerRequest(data) => {
      log.info(s"reduce($data")
      sender ! ReducerResponse(data)
    }
    case _: MemberEvent => // ignore
  }
}
