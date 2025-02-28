package com.mojang.minecraftpe.store.googleplay

import com.mojang.minecraftpe.MainActivity
import com.mojang.minecraftpe.store.ExtraLicenseResponseData
import com.mojang.minecraftpe.store.Store
import com.mojang.minecraftpe.store.StoreListener

class GooglePlayStore(
    var mActivity: MainActivity,
    licenseKey: String?,
    var mListener: StoreListener
) :
    Store {
    init {
        mListener.onStoreInitialized(true)
    }

    override val storeId: String
        get() = "android.googleplay"

    override fun hasVerifiedLicense(): Boolean {
        return true
    }

    override fun queryProducts(productIds: Array<String?>?) {
    }

    override fun acknowledgePurchase(receipt: String?, productType: String?) {
    }

    override fun queryPurchases() {
    }

    override val productSkuPrefix: String
        get() = ""

    override val realmsSkuPrefix: String
        get() = ""

    override fun receivedLicenseResponse(): Boolean {
        return true
    }

    override fun destructor() {
    }

    override val extraLicenseData: ExtraLicenseResponseData
        get() = ExtraLicenseResponseData(0L, 0L, 0L)

    override fun purchase(productId: String?, isSubscription: Boolean, payload: String?) {
    }

    override fun purchaseGame() {
    }
}