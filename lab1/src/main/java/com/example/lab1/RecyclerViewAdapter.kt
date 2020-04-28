package com.example.lab1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(val list:ArrayList<ListData>): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        if(position % 2 == 0)
        {
            holder.itemView.setBackgroundResource(R.color.colorEvenItem)
        }
        else
        {
            holder.itemView.setBackgroundResource(R.color.colorOddItem)
        }
        holder.bindItems(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }



    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(item : ListData){
            val textView:TextView = itemView.findViewById(R.id.textview)
            val imageView:ImageView = itemView.findViewById(R.id.imageview)
            textView.text = item.text
            imageView.setImageBitmap(item.image)
        }

    }
}