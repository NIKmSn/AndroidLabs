package com.example.lab67

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

class BackFragment(product: ProductCount, adapter: ProductAdapter) : ProductFragment(product,adapter) {
    private var nameText : EditText? = null
    private var countText : TextView? = null
    private var propsText : EditText? = null

    private var buttonAdd : Button? = null
    private var buttonDel : Button? = null
    private var buttonSave : Button? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.back_fragment, null)
        nameText = view.findViewById(R.id.editName) as EditText
        countText = view.findViewById(R.id.txtCount) as TextView
        propsText = view.findViewById(R.id.editProps) as EditText

        buttonAdd = view.findViewById(R.id.btnIncrease) as Button
        buttonDel = view.findViewById(R.id.btnDecrease) as Button
        buttonSave = view.findViewById(R.id.btnSave) as Button

        refresh()

        buttonAdd!!.setOnClickListener {
            product.addCount(1)
            countText!!.text = product.getCount().toString()
        }
        buttonDel!!.setOnClickListener {
            product.addCount(-1)
            countText!!.text = product.getCount().toString()
        }
        buttonSave!!.setOnClickListener {
            product.product.name = nameText?.text.toString();
            product.product.properties = propsText?.text.toString();
            Toast.makeText(context, "Изменения сохранены", LENGTH_SHORT).show()
        }

        return view
    }

    override fun refresh() {
        nameText?.setText(product.product.name)
        countText?.text = product.getCount().toString()
        propsText?.setText(product.product.properties)
    }

    override fun clear() {
        nameText?.setText("")
        countText?.text = ""
        propsText?.setText("")
    }
}