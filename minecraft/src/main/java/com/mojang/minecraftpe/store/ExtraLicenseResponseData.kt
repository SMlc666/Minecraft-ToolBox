package com.mojang.minecraftpe.store

class ExtraLicenseResponseData(validationTime: Long, retryUntilTime: Long, retryAttempts: Long) {
    var retryAttempts: Long = 0
    var retryUntilTime: Long = 0
    var validationTime: Long = 0

    init {
        this.validationTime = validationTime
        this.retryUntilTime = retryUntilTime
        this.retryAttempts = retryAttempts
    }
}