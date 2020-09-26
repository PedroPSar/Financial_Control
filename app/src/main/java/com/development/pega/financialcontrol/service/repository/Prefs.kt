package com.development.pega.financialcontrol.service.repository

import android.content.Context
import android.content.SharedPreferences
import com.development.pega.financialcontrol.R

class Prefs(context: Context) {

    private val PREFS_FILENAME = "financial.control.prefs"
    private val OBJECTIVE_VALUE_KEY = "Objective_Value"
    private val OBJECTIVE_DESCRIPTION_KEY = "Objective_Description"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)

    private val objectiveValueDefault = context.getString(R.string.objective_value_default)
    private val objectiveDescriptionDefault = context.getString(R.string.objective_description_default)

    var objectiveValue: String
        get() = prefs.getString(OBJECTIVE_VALUE_KEY, objectiveValueDefault).toString()
        set(value) = prefs.edit().putString(OBJECTIVE_VALUE_KEY, value).apply()

    var objectiveDescription: String
        get() = prefs.getString(OBJECTIVE_DESCRIPTION_KEY, objectiveDescriptionDefault).toString()
        set(value) = prefs.edit().putString(OBJECTIVE_DESCRIPTION_KEY, value).apply()
}