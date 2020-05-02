package com.example.lab5.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.webkit.WebView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lab5.R
import com.google.gson.annotations.SerializedName
import java.net.URL

data class BreedDTO(val name: String,
                    val id: String)

data class Vote(
    val value:Int,
    val image_id:String,
    @SerializedName("id")
    val voteId:String,
    @SerializedName("created_at")
    val date: String
)

data class VoteCreate(
    @SerializedName("image_id")
    val img:String,
    val value: Int,
    @SerializedName("sub_id")
    val sub: String = ICatApi.sub_id
)

data class PhotoDTO(
    val url:String,
    val id: String
)

class Photo(dto: PhotoDTO) {
    private val url = dto.url
    val id = dto.id
    private val th = Thread {
        bitmap = BitmapFactory.decodeStream(URL(url).openStream())
    }
    private lateinit var bitmap: Bitmap

    init {
        th.start()
    }

    var liked = NetworkService.voteValue(id)

    fun attachTo(view: ImageView) {
        val a = (view.context as AppCompatActivity)
        if (th.isAlive) {
            th.join()
        }
        a.runOnUiThread { view.setImageBitmap(bitmap) }
    }

    fun toDTO(): PhotoDTO {
        return PhotoDTO(url, id)
    }
}