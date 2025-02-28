package com.mojang.minecraftpe

import android.content.Intent

internal interface FilePickerManagerHandler {
    fun startPickerActivity(intent: Intent?, i: Int)
}
