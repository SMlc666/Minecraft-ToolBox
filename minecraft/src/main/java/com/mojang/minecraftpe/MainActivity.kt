package com.mojang.minecraftpe

import android.app.NativeActivity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

abstract class MainActivity : NativeActivity(), View.OnKeyListener,
    FilePickerManagerHandler {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        throw RuntimeException("Stub!")
    }

    override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
        throw RuntimeException("Stub!")
    }

    override fun startPickerActivity(intent: Intent, i: Int) {
        throw RuntimeException("Stub!")
    }
}
