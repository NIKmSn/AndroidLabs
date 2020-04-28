package com.example.lab2

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class MainActivity() : AppCompatActivity() {

    var lastPosition = 0
    var list = ArrayList<Technology>()
    var extra: Bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        val intent = intent
        val url = intent.getStringExtra("url")
        AsyncTaskHandleJson().execute(url)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tech_list.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val i = Intent(view.context, ViewPagerActivity::class.java)
                extra.putSerializable("tech", list)
                i.putExtra("extra", extra)
                i.putExtra("position", position)
                startActivity(i)
            }
        })
    }



    inner class AsyncTaskHandleJson : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg url: String?): String {
            var text: String
            val connection = URL(url[0]).openConnection() as HttpURLConnection
            try {
                connection.connect()
                text = connection.inputStream.use {
                    it.reader().use { reader -> reader.readText() }
                }
            } finally {
                connection.disconnect()
            }
            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            handleJson(result)
        }
    }

    fun handleJson(jsonString: String?) {
        val jsonArray = JSONArray(jsonString)
        var x = 1
        while (x < jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(x)
            var helptext = ""
            if (jsonObject.has("helptext")) helptext = jsonObject.getString("helptext")
            list.add(
                Technology(
                    jsonObject.getString("graphic"),
                    jsonObject.getString("name"),
                    helptext
                )
            )
            x++
        }
        val adapter = ListAdapter(this, list)
        tech_list?.adapter = adapter

    }
}
