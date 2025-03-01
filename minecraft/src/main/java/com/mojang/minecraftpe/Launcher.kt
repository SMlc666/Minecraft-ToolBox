package com.mojang.minecraftpe

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class Launcher : MainActivity() {
    override fun onCreate(bundle: Bundle?) {
        try {
            @SuppressLint("DiscouragedPrivateApi") val addAssetPath =
                assets.javaClass.getDeclaredMethod(
                    "addAssetPath",
                    String::class.java
                )
            val mcSource = intent.getStringExtra("MC_SRC")
            addAssetPath.invoke(assets, mcSource)

            val mcSplitSrc = intent.getStringArrayListExtra("MC_SPLIT_SRC")
            if (mcSplitSrc != null) {
                for (splitSource in mcSplitSrc) {
                    addAssetPath.invoke(assets, splitSource)
                }
            }
            Handler(Looper.getMainLooper()).postDelayed({
                loadLibraries()
            }, 6000) // 延迟2秒
            super.onCreate(bundle)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun loadLibraries() {
        System.loadLibrary("c++_shared")
        System.loadLibrary("fmod")
        System.loadLibrary("minecraftpe")
        System.loadLibrary("mc")
    }

}