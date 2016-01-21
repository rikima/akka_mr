package com.rikima.mr.akka

import akka.actor.{Props, Actor, ActorLogging}
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, UnreachableMember, MemberUp}
import akka.routing.ConsistentHashingGroup

/**
  * Created by mrikitoku on 2016/01/20.
  */
object Master {
  def props(num_mappers: Int, num_reducers: Int): Props = Props(new Master(num_mappers, num_reducers))
}


class Master(num_mappers: Int = 4, num_reducers: Int = 4) extends Actor with ActorLogging {
  // mappers
  val mpaths = (1 to num_mappers).map(id => s"akka.tcp://system@127.0.0.1:2550/user/mappers/m$id")
  context.actorOf(ConsistentHashingGroup(mpaths).props(), "mapper_router")

  (1 to num_mappers).foreach { case id => context.actorOf(Props[Mapper], name = s"m$id") }

  // reducers
  val rpaths = (1 to num_reducers).map(id => s"akka.tcp://system@127.0.0.1:2550/user/reducers/r$id")
  val reducer_router = context.system.actorOf(ConsistentHashingGroup(rpaths).props, "reducer_router")

  (1 to num_reducers).foreach { case id => context.system.actorOf(Props[Reducer], name = s"r$id") }


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
      log.info("Member is Removed: {} after {}", member.address, previousStatus)


    case MapperResponse(data) =>
      log.info(s"MapperResponse($data)")
      this.reducer_router ! ReducerRequest(data)


    case ReducerResponse(data) =>
      log.info(s"ReducerResponse($data)")

    case _: MemberEvent => // ignore
  }
}
