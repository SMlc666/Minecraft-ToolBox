package com.mojang.minecraftpe.store.amazonappstore

import android.content.Context
import com.mojang.minecraftpe.store.ExtraLicenseResponseData
import com.mojang.minecraftpe.store.Store
import com.mojang.minecraftpe.store.StoreListener

class AmazonAppStore : Store {
    var mListener: StoreListener
    private var mForFireTV = false

    constructor(context: Context?, listener: StoreListener) {
        mListener = listener
    }

    constructor(context: Context?, listener: StoreListener, forFireTV: Boolean) {
        mListener = listener
        mForFireTV = forFireTV
    }

    override val storeId: String
        get() = "android.amazonappstore"

    override fun hasVerifiedLicense(): Boolean {
        return true
    }

    override fun receivedLicenseResponse(): Boolean {
        return true
    }

    override fun queryProducts(productIds: Array<String?>?) {
    }

    override fun acknowledgePurchase(receipt: String?, productType: String?) {
    }

    override fun queryPurchases() {
    }

    override val productSkuPrefix: String
        get() = if (mForFireTV) "firetv." else ""

    override val realmsSkuPrefix: String
        get() = if (mForFireTV) "firetv." else ""

    override fun destructor() {
    }

    override val extraLicenseData: ExtraLicenseResponseData
        get() = ExtraLicenseResponseData(0L, 0L, 0L)

    override fun purchase(productId: String?, isSubscription: Boolean, payload: String?) {
    }

    override fun purchaseGame() {
    }
}