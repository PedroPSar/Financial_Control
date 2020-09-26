package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.service.repository.savingsmoney.SavingsMoneyRepository

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

    private val mObjectiveValue = MutableLiveData<String>()
    var objectiveValue: LiveData<String> = mObjectiveValue

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

    fun setObjectiveValue() {
        mObjectiveValue.value = prefs.objectiveValue
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
}