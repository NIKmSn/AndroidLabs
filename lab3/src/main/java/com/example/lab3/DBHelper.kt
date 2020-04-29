package com.example.lab3

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "StudentsDB", null, 1)
{
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE Students(id integer, fullName text, addTime text)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS Students")
        onCreate(db)
    }

    fun clear()
    {
        var db = this.writableDatabase
        db.execSQL("DELETE FROM Students")
    }
}