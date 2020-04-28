package com.example.lab2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        var viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val extra = intent.extras!!.getBundle("extra")
        val extra_list: ArrayList<Technology> = extra!!.getSerializable("tech") as ArrayList<Technology>
        val position = intent.extras!!.getInt("position")
        var prevPosition = position - 1
        var nextPosition = position + 1
        if (position == 0) prevPosition = extra_list.lastIndex
        if (position == extra_list.lastIndex) nextPosition = 0

        var index = 0
        while (index < extra_list.count()){
            viewPagerAdapter.addFragment(MyFragment(extra_list[index].graphic),index.toString())
            index++
        }
        viewPager.adapter = viewPagerAdapter
        viewPager.setCurrentItem(position)

        viewPager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener(){
            override fun onPageSelected(position: Int){
                viewPager.setCurrentItem(position)
            }
        })
    }
    }
