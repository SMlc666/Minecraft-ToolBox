package com.mojang.minecraftpe

import android.annotation.SuppressLint
import android.os.Bundle

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
            super.onCreate(bundle)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        init {
            System.loadLibrary("c++_shared")
            System.loadLibrary("fmod")
            System.loadLibrary("minecraftpe")
            System.loadLibrary("mc")
            System.loadLibrary("materialbinloader")
        }
    }
}