package com.mojang.minecraftpe.store

interface StoreListener {
    fun onPurchaseCanceled(str: String?)

    fun onPurchaseFailed(str: String?)

    fun onPurchaseSuccessful(str: String?, str2: String?)

    fun onQueryProductsFail()

    fun onQueryProductsSuccess(productArr: Array<Product?>?)

    fun onQueryPurchasesFail()

    fun onQueryPurchasesSuccess(purchaseArr: Array<Purchase?>?)

    fun onStoreInitialized(z: Boolean)
}