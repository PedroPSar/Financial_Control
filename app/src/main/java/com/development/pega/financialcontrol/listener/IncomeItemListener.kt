package com.development.pega.financialcontrol.listener

import com.development.pega.financialcontrol.model.Income

interface IncomeItemListener {
    fun onEdit(id: Int)
    fun  onDelete(income: Income)
}