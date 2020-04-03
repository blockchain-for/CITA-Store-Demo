package com.slashchain.cita

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object CitaRegistry {

  // Actor protocol
  sealed trait Command
  final case class CreateStore(content: StoreContent, replyTo: ActorRef[CitaActionPerformed]) extends Command
  final case class GetStore(hash: String, replyTo: ActorRef[GetStoreResponse]) extends Command
  final case class GetStores(replyTo: ActorRef[StoreTransactions]) extends Command

  final case class GetStoreResponse(transaction: Option[String])
  final case class CitaActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty[StoreTransaction])

  private def registry(tx: Set[StoreTransaction]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateStore(content, replyTo) =>

        val saved = CitaUtils.saveStore(content)
        replyTo ! CitaActionPerformed(s"Cita Store of $content stored, and return hash: $saved .")
        registry(tx ++ saved.toOption)

      case GetStore(hash, replyTo) =>
        val response = CitaUtils.getTransaction(hash)
        replyTo ! GetStoreResponse(response)
        Behaviors.same

      case GetStores(replyTo) =>
        replyTo ! StoreTransactions(tx.toSeq)
        Behaviors.same
    }
}
