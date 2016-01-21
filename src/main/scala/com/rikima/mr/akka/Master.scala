package com.rikima.mr.akka

import akka.actor.{Props, Actor, ActorLogging}
import akka.cluster.ClusterEvent.{MemberEvent, MemberRemoved, UnreachableMember, MemberUp}
import akka.routing.{ConsistentHashingGroup}
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope

/**
  * Created by mrikitoku on 2016/01/20.
  */
object Master {
  def props(hosts: String, num_mappers: Int, num_reducers: Int): Props = Props(new Master(hosts, num_mappers, num_reducers))
}


class Master(hosts: String, num_mappers: Int = 2, num_reducers: Int = 2) extends Actor with ActorLogging {
  // mappers
  val mpaths = hosts.split(",").zipWithIndex.flatMap {
    case (h, idx) => {
      (1 to num_mappers).map {
        case c => {
          val mid = idx * num_mappers + c
          s"akka.tcp://system@$h:2550/user/mappers/m$mid"
        }
      }
    }
  }.toList
  val mapper_router = context.system.actorOf(ConsistentHashingGroup(paths=mpaths).props, "mapper_router")

  // reducers
  val rpaths = hosts.split(",").zipWithIndex.flatMap {
    case (h, idx) => {
      (1 to num_mappers).map {
          case c => {
          val rid = idx * num_reducers + c
          s"akka.tcp://system@$h:2550/user/reducers/r$rid"
        }
      }
    }
  }.toList
  val reducer_router = context.system.actorOf(ConsistentHashingGroup(paths=rpaths).props, "reducer_router")
  log.info("reducer_router:" + reducer_router.toString())


  override def preStart = {
    log.debug(s"#mapper: $num_mappers")
    log.debug(s"#reducers: $num_reducers")
    log.debug(Thread.currentThread.getName + " is started.")
  }


  override def postStop = {
    log.debug(Thread.currentThread.getName + " is stopped.")
  }

  private var num_success = 0

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)

    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)

    case MapperRequest(data) =>
      log.info(s"MapperRequest($data) is recieved!")
      this.mapper_router.tell(ConsistentHashableEnvelope(MapperRequest(data), data), self)

    case MapperResponse(data) =>
      log.info(s"MapperResponse($data) is recieved")
      this.reducer_router.tell(ConsistentHashableEnvelope(ReducerRequest(data), data), self)

    case ReducerResponse(data) =>
      log.info(s"ReducerResponse($data)")
      num_success += 1
      log.info(s"#sucess: $num_success")
    case _ => {
      //log.warning(_)
    }
  }
}
