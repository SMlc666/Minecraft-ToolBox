package com.mojang.minecraftpe.store

interface Store {
    fun acknowledgePurchase(str: String?, str2: String?)

    fun destructor()

    val extraLicenseData: ExtraLicenseResponseData?

    val productSkuPrefix: String?

    val realmsSkuPrefix: String?

    val storeId: String?

    fun hasVerifiedLicense(): Boolean

    fun purchase(str: String?, z: Boolean, str2: String?)

    fun purchaseGame()

    fun queryProducts(strArr: Array<String?>?)

    fun queryPurchases()

    fun receivedLicenseResponse(): Boolean
}