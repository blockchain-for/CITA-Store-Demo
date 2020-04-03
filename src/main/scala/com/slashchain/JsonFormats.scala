package com.slashchain

import com.slashchain.UserRegistry.ActionPerformed
import com.slashchain.cita.CitaRegistry.{CitaActionPerformed, GetStoreResponse}
import com.slashchain.cita._

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val contentJsonFormat = jsonFormat1(StoreContent)
  implicit val storeTransactionJsonFormat = jsonFormat3(StoreTransaction)
  implicit val storeTransactionsJsonFormat = jsonFormat1(StoreTransactions)

  implicit val citaActionPerformedJsonFormat = jsonFormat1(CitaActionPerformed)
  implicit val citaStoreResponseJsonFormat = jsonFormat1(GetStoreResponse)
}
//#json-formats
