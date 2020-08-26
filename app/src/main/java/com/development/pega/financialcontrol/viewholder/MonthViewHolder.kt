package com.development.pega.financialcontrol.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Month
import kotlinx.android.synthetic.main.month_recycler_view_row.view.*

class MonthViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(month: Month) {
        val name = itemView.findViewById<TextView>(R.id.lbl_month_name)
        val txtAccountBalance = itemView.findViewById<TextView>(R.id.txt_account_balance)
        val txtIncomes = itemView.findViewById<TextView>(R.id.txt_incomes)
        val txtExpenses = itemView.findViewById<TextView>(R.id.txt_expenses)

        name.text = month.name
        txtAccountBalance.text = month.accountBalance
        txtIncomes.text = month.incomes
        txtExpenses.text = month.expenses
    }
}