package com.slashchain.cita

case class StoreContent(content: String)
case class StoreTransaction(content: String, createAt: Long, hash: String)
case class StoreTransactions(txs: Seq[StoreTransaction])
