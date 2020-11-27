package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import java.text.SimpleDateFormat
import java.util.*

class AddIncomeViewModel(application: Application): AndroidViewModel(application) {

    private val context = application.applicationContext
    private val mIncomeRepository = IncomeRepository(context)
    private val mSavingsMoneyRepository = SavingsMoneyRepository(context)


    private var mCurrentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = mCurrentTime

    private var mDatePickerDialog = MutableLiveData<DatePickerDialog>()
    val datePickerDialog: LiveData<DatePickerDialog> = mDatePickerDialog

    private var mAddIncome = MutableLiveData<Boolean>()
    val addIncome: LiveData<Boolean> = mAddIncome

    private val mGetIncome = MutableLiveData<Income>()
    var getIncome: LiveData<Income> = mGetIncome

    fun getCurrentDate() {
        val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
        mCurrentTime.value =  sdf.format(Date())
    }

    fun showDatePickerDialog(context: Context) {
        val c = Calendar.getInstance()
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val mMonth = c.get(Calendar.MONTH)
        val mYear = c.get(Calendar.YEAR)

        mDatePickerDialog.value = DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val sdf = SimpleDateFormat(Constants.PATTERNS.DATE_PATTERN)
            c.set(year, month, dayOfMonth)
            mCurrentTime.value = sdf.format(c.time)
        }, mYear, mMonth, mDay)
    }

    fun saveIncome(income: Income) {
        if(income.id == 0) {
            mAddIncome.value = mIncomeRepository.save(income)
        }else {
            mAddIncome.value = mIncomeRepository.update(income)

            if(income.name == context.getString(R.string.withdraw_name)) {
                val money = changeFromIncomeToSavings(income)
                money.id = getSavingsMoneyIdByRelationalID(income.relationalID)
                mSavingsMoneyRepository.update(money)
            }

        }

    }

    fun loadIncome(id: Int) {
        mGetIncome.value = mIncomeRepository.get(id)
    }

    private fun changeFromIncomeToSavings(income: Income): SavingsMoney {
        val money = SavingsMoney()
        money.money = income.value
        money.day = income.day
        money.month = income.month
        money.year = income.year
        money.description = income.description
        money.type = Constants.SAVINGS_MONEY.WITHDRAW
        money.relationalID = income.relationalID

        return money
    }

    private fun getSavingsMoneyIdByRelationalID(relationalID: Int): Int {
        return mSavingsMoneyRepository.getSavingsByRelationalId(relationalID).id
    }
}