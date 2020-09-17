package com.development.pega.financialcontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.viewholder.ExpensesViewHolder

class ExpensesRecyclerViewAdapter(): RecyclerView.Adapter<ExpensesViewHolder>() {

    private var expensesList: List<Expense> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.income_recycler_view_row, parent, false)
        return ExpensesViewHolder(item)
    }

    override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) {
        holder.bind(expensesList[position])
    }

    override fun getItemCount(): Int {
        return expensesList.count()
    }

    fun updateExpensesList(list: List<Expense>) {
        expensesList = list
        notifyDataSetChanged()
    }
}