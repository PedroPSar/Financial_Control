package com.development.pega.financialcontrol.listener

import com.development.pega.financialcontrol.model.Expense

interface ExpenseItemListener {
    fun onEdit(id: Int)
    fun onDelete(expense: Expense)
}