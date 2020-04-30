package com.example.lab4

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 * The configuration screen for the [DateTimerWidget] AppWidget.
 */
class DateTimerWidgetConfigureActivity : Activity() {

    companion object{
        val WIDGET_PREF = "widget_pref"
        val WIDGET_TIME = "widget_time_"
        val WIDGET_TIME_UNIX = "widget_tu_"
    }


    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var resultValue: Intent
    private lateinit var sp: SharedPreferences
    private lateinit var calendar: CalendarView
    private var date:Long = Date().time

    private val testCalendar: Calendar = Calendar.getInstance().apply {
        set(
            get(Calendar.YEAR),
            get(Calendar.MONTH),
            get(Calendar.DAY_OF_MONTH),
            9, 0, 0)  /// For testing - change time here
        set(Calendar.MILLISECOND, 0)
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        setContentView(R.layout.date_timer_widget_configure)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }


        resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        setResult(AppCompatActivity.RESULT_CANCELED, resultValue)

        setContentView(R.layout.date_timer_widget_configure)

        sp = getSharedPreferences(WIDGET_PREF, AppCompatActivity.MODE_PRIVATE)
        calendar = findViewById<CalendarView>(R.id.calendarView)

        val curDate = Calendar.getInstance()
        calendar.minDate = (
                if (curDate.time.after(testCalendar.time) &&
                    curDate[Calendar.DAY_OF_MONTH] == testCalendar[Calendar.DAY_OF_MONTH] &&
                    curDate[Calendar.MONTH] == testCalendar[Calendar.MONTH] &&
                    curDate[Calendar.YEAR] == testCalendar[Calendar.YEAR]
                ) curDate.apply {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
                else curDate
                ).apply {
                set(Calendar.HOUR, testCalendar[Calendar.HOUR])
                set(Calendar.MILLISECOND, testCalendar[Calendar.MILLISECOND])
                set(Calendar.SECOND, testCalendar[Calendar.SECOND])
                set(Calendar.MINUTE, testCalendar[Calendar.MINUTE])
            }.timeInMillis

        calendar.setOnDateChangeListener{ _, year, month, dayOfMonth ->
            val c = Calendar.getInstance()
            c.set(year, month, dayOfMonth,
                testCalendar[Calendar.HOUR],
                testCalendar[Calendar.MINUTE],
                testCalendar[Calendar.SECOND]
            )
            c.set(Calendar.MILLISECOND, testCalendar[Calendar.MILLISECOND])
            date = c.timeInMillis
        }
        calendar.date = sp.getLong(WIDGET_TIME_UNIX+appWidgetId, curDate.timeInMillis)
        date = calendar.date

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            val chosen = Calendar.getInstance().apply { timeInMillis = date }
            if (chosen.time.after(testCalendar.time) &&
                chosen[Calendar.DAY_OF_MONTH] == testCalendar[Calendar.DAY_OF_MONTH] &&
                chosen[Calendar.MONTH] == testCalendar[Calendar.MONTH] &&
                chosen[Calendar.YEAR] == testCalendar[Calendar.YEAR]
            ) {
                Toast.makeText(
                    this,
                    "Wrong date",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            sp.edit().apply {
                putString(WIDGET_TIME + appWidgetId, Date(date).toString())
                putLong(WIDGET_TIME_UNIX + appWidgetId, date)
            }.apply()
            setResult(AppCompatActivity.RESULT_OK, resultValue)
            val appWidgetManager = AppWidgetManager.getInstance(this)
            DateTimerWidget().onUpdate(this, appWidgetManager, IntArray(1) { appWidgetId })
            finish()
        }
    }

}