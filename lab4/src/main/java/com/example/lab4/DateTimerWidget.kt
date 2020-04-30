package com.example.lab4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [DateTimerWidgetConfigureActivity]
 */
class DateTimerWidget : AppWidgetProvider() {

    companion object{
        private val MILS_IN_DAY = 1000 * 3600 * 24
        private val NOTIFICATION_CHANEL_ID = "DateReached"
    }


    private var timers: HashMap<Int, CountDownTimer> = HashMap()


    private fun getTimerText(date: Long): String {
        var res = ""
        val dif = getDateDif(date)

        val days = dif / MILS_IN_DAY
        val hours = dif / (MILS_IN_DAY / 24)
        val mins = dif / (MILS_IN_DAY / 60 / 24)
        val rHours = (hours - 24 * days).toString()
        val rMin = (mins - 60 * hours ).toString()


        if (dif > MILS_IN_DAY) res += days.toString() + "d "
        res +=  (if (rHours.length < 2) "0" else "") + "$rHours:"
        res += (if(rMin.length < 2) "0" else "") + rMin

        return res

    }

    private fun getDateDif(date: Long): Long {
        val c = Calendar.getInstance()
        c.set(Calendar.MILLISECOND, 0)
        return date - c.timeInMillis
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        val sp = context?.getSharedPreferences(
            DateTimerWidgetConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE
        )
        for(id in appWidgetIds!!){
            if(id in timers.keys){
                timers[id]?.cancel()
            }

            val widgetView = RemoteViews(
                context?.packageName,
                R.layout.date_timer_widget
            )
            widgetView.setTextViewText(
                R.id.to,
                context?.getString(
                    R.string.time_to,
                    sp?.getString(
                        DateTimerWidgetConfigureActivity.WIDGET_TIME+id,
                        null
                    )
                )
            )
            val intent = Intent(context, DateTimerWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            widgetView.setOnClickPendingIntent(
                R.id.widgetId,
                PendingIntent.getActivity(
                    context!!, id,
                    intent,
                    0
                )
            )

            appWidgetManager?.updateAppWidget(id, widgetView)

            val timer = object : CountDownTimer(
                getDateDif(
                    sp?.getLong(DateTimerWidgetConfigureActivity.WIDGET_TIME_UNIX + id, Long.MAX_VALUE)?: Long.MAX_VALUE),
                1000*60
            ) {


                override fun onTick(millisUntilFinished: Long) {
                    widgetView.setTextViewText(
                        R.id.timer,
                        getTimerText(sp?.getLong(
                            DateTimerWidgetConfigureActivity.WIDGET_TIME_UNIX + id,
                            0)?: 0
                        )
                    )
                    appWidgetManager?.updateAppWidget(id, widgetView)
                }

                override fun onFinish() {
                    val intent = Intent(context, DateTimerWidgetConfigureActivity::class.java)
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                    val builder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANEL_ID)
                        .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                        .setContentTitle("The date has come")
                        .setContentText("The date "+
                                sp?.getString(
                                    DateTimerWidgetConfigureActivity.WIDGET_TIME + id,
                                    Date().toString()
                                )
                                + " has come"
                        )
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(
                            PendingIntent.getActivity(
                                context, id,
                                intent, 0
                            ))
                        .setVibrate(LongArray(5) { i -> arrayOf(1000L, 500L, 1000L, 250L, 1000L)[i]})
                    val notification = builder.build()
                    val notificationManager: NotificationManager =
                        ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
                    notificationManager.notify(id, notification)

                    widgetView.setTextViewText(
                        R.id.timer,
                        "Passed"
                    )
                    appWidgetManager?.updateAppWidget(id, widgetView)
                }

            }
            timers[id] = timer
            timer.start()

        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val sp = context?.getSharedPreferences(
            DateTimerWidgetConfigureActivity.WIDGET_PREF, Context.MODE_PRIVATE
        )
        appWidgetIds?.forEach {
            timers[it]?.cancel()
            sp?.edit()?.apply {
                remove(DateTimerWidgetConfigureActivity.WIDGET_TIME_UNIX + it)
                remove(DateTimerWidgetConfigureActivity.WIDGET_TIME + it)
            }?.apply()
        }
    }

    override fun onEnabled(context: Context?) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val chanel = NotificationChannel(
                NOTIFICATION_CHANEL_ID,
                "Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                ContextCompat.getSystemService(context!!, NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(chanel)
        }

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}