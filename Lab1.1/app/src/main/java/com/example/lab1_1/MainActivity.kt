package com.example.lab1_1

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private val items = ArrayList<ListData>()
    private val adapter = RecyclerViewAdapter(items)
    private lateinit var recyclerView : RecyclerView
    private lateinit var image: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById<RecyclerView>(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        image = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_round)
        addRange(100);
        smoothRefreshRV()
    }

    private fun addRange(quantity: Int) {
        if (adapter.itemCount + quantity > 1000000) {
            val myToast = Toast.makeText(this, "Нельзя добавить больше 1 000 000 элементов", Toast.LENGTH_SHORT)
            myToast.show()
        }
        else for(i in (adapter.itemCount+1)..(adapter.itemCount+quantity)) {
            val item: ListData = ListData(
                intToString(i),
                image
            )
            items.add(item)
        }
    }

    fun intToString(n: Int) : String
    {
        var str = ""
        var temp : Int = n
        var f = false
        val digits : Array<String> = arrayOf("", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
        val str11: Array<String> = arrayOf("десять", "одиннадцать", "двеннадцать", "тринадцать","четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать")
        val str10: Array<String> = arrayOf("", "", "двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяноста")
        val hundreds: Array<String> = arrayOf("", "сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот")
        val thousands: Array<String> = arrayOf("", "тысяча", "две тысячи", "три тысячи", "четыре тысячи", "пять тысяч", "шесть тысяч", "семь тысяч", "восемь тысяч", "девять тысяч")

        if (temp/1000000 > 0)
            return "Один миллион"
        temp %= 1000000
        if (temp/100000 > 0)
        {
            f = true;
            str += hundreds[n/100000 % 10] + " "
        }
        temp %= 100000

        if (temp/10000 > 0)
        {
            if (temp/10000 == 1)
            {
                str += str11[temp/1000 % 10] + " "
                temp %= 1000
                f = true
            }
            else
            {
                str+= str10[temp/10000] + " "
                temp %= 10000
                f = true
            }
        }
        if (temp/1000 > 0)
        {
            str += thousands[temp/1000] + " "
        }
        else if (f)
        {
            str += "тысяч "
        }
        temp %= 1000
        if (temp/100 > 0)
        {
            str += hundreds[temp/100] + " "
        }

        temp %= 100
        if (temp/10 > 0)
        {
            if (temp/10 == 1)
            {
                str += str11[temp%10] + " "
                return str
            }
            else
            {
                str += str10[temp/10] + " " + digits[temp%10] + " "
                return str
            }
        }
        str += digits[temp%10] + " "
        return str
    }

    private fun smoothRefreshRV() {
        recyclerView.adapter?.notifyItemInserted(adapter.itemCount)
        recyclerView.smoothScrollToPosition(adapter.itemCount)
    }

    private fun refreshRV() {
        recyclerView.adapter?.notifyItemInserted(adapter.itemCount)
        recyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    fun add100(view: View) {
        addRange(100)
        smoothRefreshRV()
    }

    fun add1K(view: View) {
        addRange(1000)
        refreshRV()
    }
    fun add100K(view: View) {
        addRange(100000)
        refreshRV()
    }

}
