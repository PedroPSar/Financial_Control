package com.development.pega.financialcontrol.viewholder

import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.model.Income

class ExpensesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnTouchListener {

    fun bind(expense: Expense) {
        val txtDate = "${expense.day}/${expense.month}/${expense.year}"
        val txtValue = AppControl.Text.convertFloatToCurrencyText(expense.value)
        val txtName = expense.name

        itemView.findViewById<TextView>(R.id.tv_txt_name).text = txtName
        itemView.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        itemView.findViewById<TextView>(R.id.tv_txt_value).text = txtValue

        itemView.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return if (event != null && v != null) {
            AppControl.recyclerItemTouch(event.action, v)
        }else {
            false
        }
    }
}