package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import kotlin.math.roundToInt

class SavingsViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val savingsMoneyRepository = SavingsMoneyRepository(mContext)
    private val mExpenseRepository = ExpenseRepository(mContext)
    private val mIncomeRepository = IncomeRepository(mContext)
    private val prefs = AppControl.getAppPrefs(mContext)

    private val mDepositRecyclerViewInfo = MutableLiveData<List<SavingsMoney>>()
    var depositRecyclerViewInfo: LiveData<List<SavingsMoney>> = mDepositRecyclerViewInfo

    private val mWithdrawalsRecyclerViewInfo = MutableLiveData<List<SavingsMoney>>()
    var withdrawalsRecyclerViewInfo: LiveData<List<SavingsMoney>> = mWithdrawalsRecyclerViewInfo

    private val mDepositsTotal = MutableLiveData<Float>()
    var depositsTotal: LiveData<Float> = mDepositsTotal

    private val mWithdrawalsTotal = MutableLiveData<Float>()
    var withdrawalsTotal: LiveData<Float> = mWithdrawalsTotal

    private val mSavingsAmount = MutableLiveData<String>()
    var savingsAmount: LiveData<String> = mSavingsAmount

    private val mAmountPercent = MutableLiveData<String>()
    var amountPercent: LiveData<String> = mAmountPercent

    private val mAmountProgressBar = MutableLiveData<Int>()
    var amountProgressBar: LiveData<Int> = mAmountProgressBar

    private val mObjectiveDescription = MutableLiveData<String>()
    var objectiveDescription: LiveData<String> = mObjectiveDescription

    fun setDepositRecyclerViewInfo() {
        val savingsMoneyList = savingsMoneyRepository.getDeposits()
        val sortedList = AppControl.orderSavingsMoney(savingsMoneyList)
        mDepositRecyclerViewInfo.value = sortedList
    }

    fun setWithdrawalsRecyclerViewInfo() {
        val savingsMoneyList = savingsMoneyRepository.getWithdrawals()
        val sortedList = AppControl.orderSavingsMoney(savingsMoneyList)
        mWithdrawalsRecyclerViewInfo.value = sortedList
    }

    fun setDepositsTotal() {
        val depositList = mDepositRecyclerViewInfo.value
        if(depositList != null) {
            mDepositsTotal.value = calcTotalSavings(depositList)
        }
    }

    fun setWithdrawalsTotal() {
        val withdrawalsList = mWithdrawalsRecyclerViewInfo.value
        if(withdrawalsList != null) {
            mWithdrawalsTotal.value = calcTotalSavings(withdrawalsList)
        }
    }

    fun setSavingsAmount() {
        val deposits = mDepositsTotal.value
        val withdrawals = mWithdrawalsTotal.value
        var amountValue = 0f

        if(deposits != null && withdrawals != null) {
            amountValue = (deposits - withdrawals)
        }else if(deposits == null && withdrawals == null) {
            amountValue = 0f
        }else if(withdrawals == null && deposits != null) {
            amountValue = deposits
        }else if(deposits == null && withdrawals != null) {
            amountValue = (0f - withdrawals)
        }

        mSavingsAmount.value = "${AppControl.Text.convertFloatToCurrencyText(amountValue)} / ${AppControl.Text.convertFloatToCurrencyText(getObjectiveValue())}"

        setAmountPercent(amountValue, getObjectiveValue())
    }

    private fun getObjectiveValue(): Float {
        return AppControl.Text.convertCurrencyTextToFloat(prefs.objectiveValue)
    }

    private fun setAmountPercent(amountValue: Float, objectiveValue: Float) {

        var percent = (amountValue / objectiveValue) * 100f
        if(percent > 100f){
            percent = 100f
        }

        if(!percent.isNaN()) {
            mAmountPercent.value = percent.roundToInt().toString() + "%"
            mAmountProgressBar.value = percent.roundToInt()
        }
    }

    fun setObjectiveDescription() {
        mObjectiveDescription.value = prefs.objectiveDescription
    }

    private fun calcTotalSavings(list: List<SavingsMoney>): Float {
        var total = 0f
        for(savingMoney in list) {
            total += savingMoney.money
        }
        return total
    }

    fun saveObjectiveValue(value: String) {
        prefs.objectiveValue = value
    }

    fun saveObjectiveDescription(description: String) {
        prefs.objectiveDescription = description
    }

    fun deleteSavingsMoney(savingsMoney: SavingsMoney) {
        savingsMoneyRepository.delete(savingsMoney)

        if(savingsMoney.type == Constants.SAVINGS_MONEY.DEPOSIT) {

            val expenseID = getExpenseIdByRelationalID(savingsMoney.relationalID)
            val expense = mExpenseRepository.get(expenseID)
            mExpenseRepository.delete(expense)

        } else {
            val incomeID = getIncomeIdByRelationalID(savingsMoney.relationalID)
            val income = mIncomeRepository.get(incomeID)
            mIncomeRepository.delete(income)
        }
    }

    private fun getIncomeIdByRelationalID(relationalID: Int): Int {
        return mIncomeRepository.getIncomeByRelationalId(relationalID).id
    }

    private fun getExpenseIdByRelationalID(relationalID: Int): Int {
        return mExpenseRepository.getExpenseByRelationalId(relationalID).id
    }

}