package com.development.pega.financialcontrol.control

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.development.pega.financialcontrol.service.Constants
import java.text.SimpleDateFormat
import java.util.*

abstract class AppControl {

    companion object {
        fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun getRecurrence(pos: Int): Int {
            return when(pos) {
                Constants.RECURRENCE.NONE -> {Constants.RECURRENCE.NONE}
                Constants.RECURRENCE.INSTALLMENT -> {Constants.RECURRENCE.INSTALLMENT}
                Constants.RECURRENCE.FIXED_MONTHLY -> {Constants.RECURRENCE.FIXED_MONTHLY}
                else -> {Constants.RECURRENCE.NONE}
            }
        }

        fun getType(pos: Int): Int {
            return when(pos) {
                Constants.TYPE.NOT_REQUIRED -> {Constants.TYPE.NOT_REQUIRED}
                Constants.TYPE.REQUIRED -> {Constants.TYPE.REQUIRED}
                Constants.TYPE.INVESTMENT -> {Constants.TYPE.INVESTMENT}
                else -> {Constants.TYPE.NOT_REQUIRED}
            }
        }

        fun calendarSetTime(date: String): Calendar {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
            c.time = sdf.parse(date)
            return c
        }
    }

}