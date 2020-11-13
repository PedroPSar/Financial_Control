package com.development.pega.financialcontrol.service.repository

import android.content.Context
import android.content.SharedPreferences
import com.development.pega.financialcontrol.R

class Prefs(context: Context) {

    private val PREFS_FILENAME = "financial.control.prefs"
    private val OBJECTIVE_VALUE_KEY = "Objective_Value"
    private val OBJECTIVE_DESCRIPTION_KEY = "Objective_Description"
    private val CURRENCY_SELECTED_KEY = "Currency_Selected"
    private val RELATIONAL_ID_KEY = "RelationalID_Key"

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_FILENAME,
        Context.MODE_PRIVATE
    )

    private val objectiveValueDefault = context.getString(R.string.objective_value_default)
    private val objectiveDescriptionDefault = context.getString(R.string.objective_description_default)
    private val currencySelectedDefault = context.getString(R.string.currency_selected_default)
    //private val relationalIDDefault = context.getString(R.string.relationalIDDefault)
    private val relationalIDDefault = 0

    var objectiveValue: String
        get() = prefs.getString(OBJECTIVE_VALUE_KEY, objectiveValueDefault).toString()
        set(value) = prefs.edit().putString(OBJECTIVE_VALUE_KEY, value).apply()

    var objectiveDescription: String
        get() = prefs.getString(OBJECTIVE_DESCRIPTION_KEY, objectiveDescriptionDefault).toString()
        set(value) = prefs.edit().putString(OBJECTIVE_DESCRIPTION_KEY, value).apply()

    var currencySelectedValue: String
        get() = prefs.getString(CURRENCY_SELECTED_KEY, currencySelectedDefault).toString()
        set(value) = prefs.edit().putString(CURRENCY_SELECTED_KEY, value).apply()

    var relationalID: Int
        get() = prefs.getInt(RELATIONAL_ID_KEY, relationalIDDefault)
        set(value) = prefs.edit().putInt(RELATIONAL_ID_KEY, value).apply()
}