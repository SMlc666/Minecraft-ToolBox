package io.bambosan.mbloader

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Fallback : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fallback)
        val logOut = findViewById<TextView>(R.id.logOut)
        val log = intent.getStringExtra("LOG_STR")
        logOut.text = log
    }
}