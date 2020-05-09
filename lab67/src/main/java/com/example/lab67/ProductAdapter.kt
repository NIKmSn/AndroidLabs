package com.example.lab67

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

class ProductAdapter(fm: FragmentManager?, val pager:ViewPager, private val products: MutableList<ProductCount>, private val construct : (ProductCount, ProductAdapter)-> ProductFragment) : FragmentPagerAdapter(fm!!, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var all = mutableListOf<ProductFragment>();

    init {
        pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {

                if (position >= 0 && position < all.size && all[position].mustBeRefreshed) {
                    all[position].refresh()
                    all[position].mustBeRefreshed = false
                }
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (position >= 0 && position < all.size && all[position].mustBeRefreshed) {
                    all[position].refresh()
                    all[position].mustBeRefreshed = false
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        refresh()
    }

    override fun getItem(position: Int): Fragment {
        return all[position]
    }

    override fun getCount(): Int {
        return all.size;
    }

    fun refresh(mainThread: Boolean = true) {
        if (all.size < products.size) {
            for (i in all.size until products.size)
                all.add(construct(products[i], this))
            notifyDataSetChanged()
        }
        if (all.size > products.size) {
            while (all.size != products.size) {
                if (mainThread)
                    all[all.size - 1].clear()
                all.removeAt(all.size - 1)
            }
            notifyDataSetChanged()
        }


        for (i in 0 until all.size) {
            all[i].product = products[i]
            all[i].mustBeRefreshed = true
        }

        if (mainThread && all.size != 0) {
            if (pager.currentItem >= all.size)
                pager.currentItem--
            all[pager.currentItem].refresh()
            all[pager.currentItem].mustBeRefreshed = false;
        }

    }
}