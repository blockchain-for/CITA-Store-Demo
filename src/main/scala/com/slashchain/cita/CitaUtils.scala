package com.slashchain.cita

import java.math.BigInteger
import java.time.Instant

import com.citahub.cita.protobuf.{Blockchain, ConvertStrByte}
import com.citahub.cita.protocol.CITAj
import com.citahub.cita.protocol.core.methods.request.Transaction
import com.citahub.cita.protocol.core.DefaultBlockParameterName
import com.citahub.cita.protocol.core.methods.response.AppMetaData.AppMetaDataResult
import com.citahub.cita.protocol.http.HttpService
import com.citahub.cita.protocol.system.CITAjSystemContract
import com.citahub.cita.utils.{Numeric => NumericCITA}
import com.slashchain.config.{AppConfig, CitaConfig}

import scala.util._

object CitaUtils {

  lazy val citaConf: CitaConfig = AppConfig.settings.app.cita

  def buildService(citaConf: CitaConfig, debugMode: Boolean = false): CITAj = {
    HttpService.setDebug(debugMode)
    CITAj.build(new HttpService(citaConf.ipAddr))
  }

  def getAppMetaDataResult(service: CITAj): Try[AppMetaDataResult] = Try {
    service.appMetaData(DefaultBlockParameterName.PENDING).send().getAppMetaDataResult
  }

  def getChainIdAndVersion(service: CITAj): Try[(BigInteger, Int)] = {
    val res = getAppMetaDataResult(service)
    res.map(r => (r.getChainId(), r.getVersion()))
  }

  def saveStore(content: StoreContent, citaConf: CitaConfig = citaConf): Try[StoreTransaction] = {
    println(s"the content: $content saving to cita ......")
    val service = buildService(citaConf)

    getChainIdAndVersion(service).collect {
      case (chainId, version) =>
        //val tx = buildTransaction(service, content, chainId, version)
        sendTransaction(service, content, citaConf, chainId, version)
    }
  }


  def buildTransaction(service: CITAj, content: StoreContent, chainId: BigInteger, version: Int): Transaction = {
    val dataToStore = ConvertStrByte.stringToHexString(content.content)
    val systemContract = new CITAjSystemContract(service)

    systemContract.constructStoreTransaction(dataToStore, version, chainId)

  }

  /**
   * 发送存证，返回交易的Hash
   * @param service CITAj
   * @param content StoreContent
   * @return transaction hash
   */
  def sendTransaction(service: CITAj, content: StoreContent,
                      citaConf: CitaConfig, chainId: BigInteger, version: Int): StoreTransaction = {

    val tx = buildTransaction(service, content, chainId, version)
    val cryptoTx: Transaction.CryptoTx = Transaction.CryptoTx.valueOf(citaConf.cryptoType)

    val payerKey = citaConf.adminPrivateKey

    val signedTx = tx.sign(payerKey, cryptoTx, false)

    val appSendTransaction = service.appSendRawTransaction(signedTx).send()

    val hash = appSendTransaction.getSendTransactionResult.getHash

    println(s"Wait 6s for transaction: $hash written into block.")

    StoreTransaction(content.content, Instant.now().toEpochMilli, hash)
  }

  def getTransaction(service: CITAj, hash: String): Option[String] = {
    val serializedTx = Option(service.appGetTransactionByHash(hash).send().getTransaction().getContent())
    println(s"Unverified transaction(Content transaction): $serializedTx")

    val byteSerializedTx = serializedTx.map(
      content => ConvertStrByte.hexStringToBytes(NumericCITA.cleanHexPrefix(content))
    )

    val unverifiedTransaction = byteSerializedTx.map(Blockchain.UnverifiedTransaction.parseFrom)

    println(s"The $hash unverified transaction is: $unverifiedTransaction")

    val dataStored = unverifiedTransaction.map(_.getTransaction.getData.toStringUtf8)

    println(s"The $hash transaction data is: $dataStored")

    dataStored
  }

  def getTransaction(hash: String): Option[String] = {
    val service = buildService(citaConf)
    getTransaction(service, hash)
  }
}
