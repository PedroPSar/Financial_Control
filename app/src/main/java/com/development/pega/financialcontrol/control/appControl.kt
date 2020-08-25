package com.development.pega.financialcontrol.control

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.development.pega.financialcontrol.service.Constants

abstract class appControl {

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
    }

}