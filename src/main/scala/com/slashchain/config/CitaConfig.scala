package com.slashchain.config

case class CitaConfig(
                       chainId: Int,
                       version: Int,
                       ipAddr: String,
                       privatePrivKey: String,
                       primaryAddr: String,
                       auxPrivKey1: String,
                       auxAddr1: String,
                       auxPrivKey2: String,
                       auxAddr2: String,
                       tokenSolidity: String,
                       tokenBin: String,
                       tokenAbi: String,
                       simpleBin: String,
                       defaultQuotaTransfer: Int,
                       defaultQuotaDeployment: Int,
                       adminPrivateKey: String,

                       adminAddress: String,
                       cryptoType: String
                     )
