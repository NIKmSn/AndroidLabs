package com.example.lab67

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class OnBuy(val adapter: ProductAdapter, val product : ProductCount) {

    fun OnSuccess(){
        adapter.refresh(false)
    }
    fun onError(){
        adapter.refresh(false)
    }
}