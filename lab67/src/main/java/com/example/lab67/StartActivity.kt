package com.example.lab67

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    fun toMainButton(view: View?): Unit {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    fun toBackendButton(view: View?) {
        val intent = Intent(this, BackActivity::class.java)
        this.startActivity(intent)
    }
}
