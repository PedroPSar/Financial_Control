package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository
import kotlin.math.roundToInt

class SavingsViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val savingsMoneyRepository = SavingsMoneyRepository(mContext)
    private val prefs = Prefs(mContext)

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

    private val mObjectiveDescription = MutableLiveData<String>()
    var objectiveDescription: LiveData<String> = mObjectiveDescription

    fun setDepositRecyclerViewInfo() {
        mDepositRecyclerViewInfo.value = savingsMoneyRepository.getDeposits()
    }

    fun setWithdrawalsRecyclerViewInfo() {
        mWithdrawalsRecyclerViewInfo.value = savingsMoneyRepository.getWithdrawals()
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

}