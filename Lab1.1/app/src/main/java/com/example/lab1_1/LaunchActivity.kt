package com.example.lab1_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(2000)
                    val i = Intent(baseContext, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        background.start()
    }


}
