package com.example.lab3

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.random.Random

class MainActivity : AppCompatActivity() , View.OnClickListener {

    private val studentList = arrayListOf<String>(
        "Алексеев Никита Евгеньевич",
        "Баранников Вадим Сергеевич",
        "Булыгин Андрей Геннадьевич",
        "Геденидзе Давид Темуриевич",
        "Горак Никита Сергеевич",
        "Грачев Александр Альбертович",
        "Гусейнов Илья Алексеевич",
        "Жарикова Екатерина Сергеевна",
        "Журавлев Владимир Евгеньевич",
        "Иванов Алексей Дмитриевич",
        "Карипова Лейсан Вильевна",
        "Копотов Михаил Алексеевич",
        "Копташкина Татьяна Алексеевна",
        "Косогоров Кирилл Станиславович",
        "Кошкин Артем Сергеевич",
        "Логецкая Светлана Александровна",
        "Магомедов Мурад Магамедович",
        "Миночкин Антон Андреевич",
        "Опарин Иван Алексеевич",
        "Паршаков Никита Алексеевич",
        "Самохин Олег Романович",
        "Сахаров Владислав Игоревич",
        "Смирнов Сергей Юрьевич",
        "Трошин Дмитрий Вадимович",
        "Чехуров Денис Александрович",
        "Эльшейх Самья Ахмед",
        "Юров Илья Игоревич",
        "Загребельный Александр Русланович")
    lateinit var btnRead : Button
    lateinit var btnAdd : Button
    lateinit var btnReplace : Button
    lateinit var myHelper : DBHelper
    lateinit var myText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRead = findViewById(R.id.btnRead)
        btnRead.setOnClickListener(this)

        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener(this)

        btnReplace = findViewById(R.id.btnReplace)
        btnReplace.setOnClickListener(this)

        myText = findViewById(R.id.txtInsert)

        myHelper = DBHelper(this)
        initDB()
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRead -> {
                var db = myHelper.readableDatabase
                val cursor = db.rawQuery("SELECT * FROM Students", null)
                val intent = Intent(this, StudentActivity::class.java)
                startActivity(intent)
            }
            R.id.btnAdd -> {
                val fullName = myText.text.toString()
                insertToDB(fullName)
                myText.text.clear()
            }
            R.id.btnReplace -> {
                var db = myHelper.readableDatabase
                db.execSQL("DELETE FROM Students WHERE id = (SELECT MAX(id) FROM Students)")
                insertToDB("Иванов Иван Иванович")
            }
        }
    }



    fun initDB()
    {
        myHelper.clear()
        for (i in 0 .. 5)
        {
            insertToDB(studentList.get(Random.nextInt(0, studentList.size)))
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    fun insertToDB(fullName: String)
    {
        var dbWriter = myHelper.writableDatabase
        var dbReader = myHelper.readableDatabase
        val cursor = dbReader.rawQuery("SELECT * FROM Students", null)
        val values = ContentValues().apply {
            put("id", cursor.count)
            put("fullName", fullName)
            put("addTime", LocalDateTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)).removeSuffix(" AM").removeSuffix(" PM").toString())
        }
        dbWriter?.insert("Students", null, values)
    }

}
