package com.development.pega.financialcontrol.listener

import com.development.pega.financialcontrol.model.SavingsMoney

interface SavingsMoneyItemListener {
    fun onEdit(id: Int)
    fun  onDelete(savingsMoney: SavingsMoney)
}