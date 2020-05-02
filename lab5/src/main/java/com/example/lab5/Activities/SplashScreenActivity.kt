package com.example.lab5.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.example.lab5.R
import com.example.lab5.api.BreedDTO
import com.example.lab5.api.NetworkService

class SplashScreenActivity : AppCompatActivity() {

    private var changed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        if (savedInstanceState == null
            || !savedInstanceState.getBoolean("changed", false)
        ){
            Thread{
                while (NetworkService.getBreeds() == listOf<BreedDTO?>())
                    changeToMA()
            }.start()
            Thread{
                Thread.sleep(2000)
                changeToMA()
            }.start()
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean("changed", changed)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        changed = savedInstanceState.getBoolean("changed")
    }

    private fun changeToMA() {
        if (!changed) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            changed = true
            finish()
        }
    }
}
