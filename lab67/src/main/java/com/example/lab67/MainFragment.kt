package com.example.lab67

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class MainFragment(product: ProductCount, adapter: ProductAdapter) : ProductFragment(product, adapter) {
    var nameText: TextView? = null
    var countText: TextView? = null
    var propsText: TextView? = null
    var buyButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_fragment, null)
        nameText = view.findViewById(R.id.txtFrontName) as TextView
        countText = view.findViewById(R.id.txtFrontCount) as TextView
        propsText = view.findViewById(R.id.txtFrontProps) as TextView
        buyButton = view.findViewById(R.id.btnBuy) as Button

        refresh()

        buyButton?.setOnClickListener {
            product.tryBuy(OnBuy(adapter, product))
            refresh()
        }

        return view
    }

    override fun refresh() {
        nameText?.text = product.product.name
        countText?.text = product.getCount().toString()
        propsText?.text = product.product.properties
    }

    override fun clear() {
        nameText?.text = ""
        countText?.text = ""
        propsText?.text = ""
    }
}