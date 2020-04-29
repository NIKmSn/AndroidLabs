package com.example.lab3

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.items.view.*
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val items : Cursor, val context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.items, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items.moveToPosition(position)
        var string = items.getString(0) + ": " + items.getString(1) + ": " + items.getString(2)
        holder?.number?.text = string
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val number = view.item_textView
    }
}