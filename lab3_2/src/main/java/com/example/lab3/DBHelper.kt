package com.example.lab3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DBHelper(context: Context) : SQLiteOpenHelper(context, "StudentsDB", null, 1)
{
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("CREATE TABLE Students(id integer, fullName text, addTime text)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        var dbr = this.readableDatabase
        val cursor = dbr.rawQuery("SELECT * FROM Students", null)
        db!!.execSQL("DROP TABLE IF EXISTS Students")
        db!!.execSQL("CREATE TABLE Students(id integer, lastName text, firstName text, middleName text, addTime text)")
        cursor.moveToFirst()
        db!!.execSQL("")
        for (i in 0 until cursor.count)
        {
            var fullName = cursor.getString(1).split(" ")
            val values = ContentValues().apply {
                put("id", cursor.count)
                put("lastName", fullName[0])
                put("firstName", fullName[1])
                put("MiddleName", fullName[2])
                put("addTime", LocalDateTime.now().format(
                    DateTimeFormatter.ofLocalizedTime(
                        FormatStyle.SHORT)).removeSuffix(" AM").removeSuffix(" PM").toString())
            }
            db?.insert("Students", null, values)
        }
    }

    fun clear()
    {
        var db = this.writableDatabase
        db.execSQL("DELETE FROM Students")
    }
}