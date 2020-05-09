package com.example.lab67

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager

class BackActivity : AppCompatActivity() {

    var ourPager: ViewPager? = null
    var ourAdapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back)

        ourPager = findViewById(R.id.backend_pager)
        ourAdapter = ProductAdapter(supportFragmentManager, ourPager!!, STORE.products)
        {productCount: ProductCount, adapter: ProductAdapter ->  BackFragment(productCount, adapter)}
        ourPager!!.adapter = ourAdapter
    }

    fun addToStoreButton(view: View?): Unit {
        ourPager!!.isActivated = true
        STORE.addProduct(ourPager!!.currentItem)
        ourAdapter!!.refresh()
    }

    fun removeFromStoreButton(view: View?): Unit {
        if (STORE.products.size != 0) {
            STORE.removeProduct(ourPager!!.currentItem)
            ourAdapter!!.refresh()
        } else
            ourPager!!.isActivated = false
    }
}
