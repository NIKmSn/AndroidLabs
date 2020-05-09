package com.example.lab67

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity() {

    var ourPager: ViewPager? = null
    var ourAdapter: PagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ourPager = findViewById(R.id.main_pager)
        STORE.refreshEnabled()
        ourAdapter = ProductAdapter(supportFragmentManager, ourPager!!, STORE.enabledProducts)
        {productCount: ProductCount, adapter : ProductAdapter ->  MainFragment(productCount, adapter)}
        ourPager!!.adapter = ourAdapter
    }
}
