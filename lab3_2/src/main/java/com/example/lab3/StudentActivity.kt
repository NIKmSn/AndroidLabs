package com.example.lab3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentActivity : AppCompatActivity() {

    lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        recycler = findViewById(R.id.recyclerView)

        var layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager;
        recycler.setHasFixedSize(true)

        val myHelper = DBHelper(this)
        var db = myHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Students", null)
        recycler.adapter = MyAdapter(cursor, this)
    }
}
