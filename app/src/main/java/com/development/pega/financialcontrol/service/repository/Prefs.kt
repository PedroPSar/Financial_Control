package com.development.pega.financialcontrol.service.repository

import android.content.Context
import android.content.SharedPreferences
import com.development.pega.financialcontrol.R

class Prefs private constructor(context: Context) {

    companion object {

        private lateinit var INSTANCE: Prefs

        fun getInstance(context: Context): Prefs {
            if(!::INSTANCE.isInitialized) {
                INSTANCE = Prefs(context)
            }
            return INSTANCE
        }
    }

    private val PREFS_FILENAME = "financial.control.prefs"
    private val OBJECTIVE_VALUE_KEY = "Objective_Value"
    private val OBJECTIVE_DESCRIPTION_KEY = "Objective_Description"
    private val CURRENCY_SELECTED_KEY = "Currency_Selected"
    private val RELATIONAL_ID_KEY = "RelationalID_Key"
    private val INCOMES_COLOR_KEY = "Incomes_Color_Key"
    private val EXPENSES_COLOR_KEY = "Expenses_Color_Key"
    private val BALANCE_COLOR_KEY = "Balance_Color_Key"
    private val NOT_REQUIRED_COLOR_KEY = "Not_Required_Color_Key"
    private val REQUIRED_COLOR_KEY = "Required_Color_Key"
    private val INVESTMENT_COLOR_KEY = "Investment_Color_key"
    private val EXPENSES_INSTALLMENT_COLOR_KEY = "Expenses_Installment_Color_Key"
    private val EXPENSES_FIXED_MONTHLY_COLOR_KEY = "Expenses_Fixed_Monthly_Color_Key"
    private val INCOMES_INSTALLMENT_COLOR_KEY = "Incomes_Installment_Color_Key"
    private val INCOMES_FIXEDMONTHLY_COLOR_KEY = "Incomes_Fixed_Monthly_Color_Key"
    private val mContext = context

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_FILENAME,
        Context.MODE_PRIVATE
    )

    private val objectiveValueDefault = context.getString(R.string.objective_value_default)
    private val objectiveDescriptionDefault = context.getString(R.string.objective_description_default)
    private val currencySelectedDefault = context.getString(R.string.currency_selected_default)
    private val relationalIDDefault = 0

    private var incomesColorDefault = getResourceColor(R.color.colorIncomes)
    private var expensesColorDefault = getResourceColor(R.color.colorExpenses)
    private var balanceColorDefault = getResourceColor(R.color.colorBalance)
    private var notRequiredColorDefault = getResourceColor(R.color.not_required_color)
    private var requiredColorDefault = getResourceColor(R.color.required_color)
    private var investmentColorDefault = getResourceColor(R.color.investment_color)
    private var expensesInstallmentColorDefault = getResourceColor(R.color.expense_installment_color)
    private var expensesFixedMonthlyColorDefault = getResourceColor(R.color.expense_fixed_monthly_color)
    private var incomesInstallmentColorDefault = getResourceColor(R.color.income_installment_color)
    private var incomesFixedMonthlyColorDefault = getResourceColor(R.color.income_fixed_monthly_color)

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

    var incomesColor: Int
    get() = prefs.getInt(INCOMES_COLOR_KEY, incomesColorDefault)
    set(value) = prefs.edit().putInt(INCOMES_COLOR_KEY, value).apply()

    var expensesColor: Int
    get() = prefs.getInt(EXPENSES_COLOR_KEY, expensesColorDefault)
    set(value) = prefs.edit().putInt(EXPENSES_COLOR_KEY, value).apply()

    var balanceColor: Int
    get() = prefs.getInt(BALANCE_COLOR_KEY, balanceColorDefault)
    set(value) = prefs.edit().putInt(BALANCE_COLOR_KEY, value).apply()

    var notRequiredColor: Int
    get() = prefs.getInt(NOT_REQUIRED_COLOR_KEY, notRequiredColorDefault)
    set(value) = prefs.edit().putInt(NOT_REQUIRED_COLOR_KEY, value).apply()

    var requiredColor: Int
    get() = prefs.getInt(REQUIRED_COLOR_KEY, requiredColorDefault)
    set(value) = prefs.edit().putInt(REQUIRED_COLOR_KEY, value).apply()

    var investmentColor: Int
    get() = prefs.getInt(INVESTMENT_COLOR_KEY, investmentColorDefault)
    set(value) = prefs.edit().putInt(INVESTMENT_COLOR_KEY, value).apply()

    var expensesInstallmentColor: Int
    get() = prefs.getInt(EXPENSES_INSTALLMENT_COLOR_KEY, expensesInstallmentColorDefault)
    set(value) = prefs.edit().putInt(EXPENSES_INSTALLMENT_COLOR_KEY, value).apply()

    var expensesFixedMonthlyColor: Int
    get() = prefs.getInt(EXPENSES_FIXED_MONTHLY_COLOR_KEY, expensesFixedMonthlyColorDefault)
    set(value) = prefs.edit().putInt(EXPENSES_FIXED_MONTHLY_COLOR_KEY, value).apply()

    var incomesInstallmentColor: Int
    get() = prefs.getInt(INCOMES_INSTALLMENT_COLOR_KEY, incomesInstallmentColorDefault)
    set(value) = prefs.edit().putInt(INCOMES_INSTALLMENT_COLOR_KEY, value).apply()

    var incomesFixedMonthlyColor: Int
    get() = prefs.getInt(INCOMES_FIXEDMONTHLY_COLOR_KEY, incomesFixedMonthlyColorDefault)
    set(value) = prefs.edit().putInt(INCOMES_FIXEDMONTHLY_COLOR_KEY, value).apply()

    private fun getResourceColor(resColor: Int): Int {
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            mContext.getColor(resColor)
        } else {
            mContext.resources.getColor(resColor)
        }
    }

}