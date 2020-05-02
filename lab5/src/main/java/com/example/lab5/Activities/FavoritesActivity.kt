package com.example.lab5.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab5.R
import com.example.lab5.api.NetworkService
import com.example.lab5.api.Photo
import com.example.lab5.api.Vote

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val rv = findViewById<RecyclerView>(R.id.rv_item_favorites)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = LikedAdapter(this)
        (rv.adapter as LikedAdapter).notifyDataSetChanged()
        }

    class LikedAdapter(private val context: AppCompatActivity)
        : RecyclerView.Adapter<LikedAdapter.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)

        private var list: List<Vote> = NetworkService.lastTenLikes().filterNotNull().toList()
        private var pics: List<Photo> = listOf()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.recycler_view_item, parent, false)
            return ViewHolder(
                view
            )
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Thread {
                val ph = if (position < pics.size){
                    pics[position]
                }else{
                    val dto =
                        NetworkService.retrofit.getImgById(list[position].image_id).execute().body()!!
                    pics = pics + Photo(dto)
                    pics[position]
                }
                ph.attachTo(holder.img)
                context.runOnUiThread {
                    holder.like.setImageDrawable(context.getDrawable(R.drawable.like_pressed))
                }
                holder.like.setOnClickListener {
                    NetworkService.delVote(ph.id)
                    ph.liked = -1
                    list = NetworkService.lastTenLikes().filterNotNull().toList()
                    pics = pics - pics[position]
                    context.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
                holder.dis.setOnClickListener {
                    NetworkService.vote(ph.id, 0)
                    ph.liked = 0
                    list = NetworkService.lastTenLikes().filterNotNull().toList()
                    pics = pics - pics[position]
                    context.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
            }.start()

        }

        class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val img: ImageView = view.findViewById(R.id.img)
            val like: ImageButton = view.findViewById(R.id.btn_like)
            val dis: ImageButton = view.findViewById(R.id.btn_dislike)
        }
    }
}
