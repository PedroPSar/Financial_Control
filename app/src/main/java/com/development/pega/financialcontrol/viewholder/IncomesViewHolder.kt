package com.development.pega.financialcontrol.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Income

class IncomesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(income: Income) {
        val txtDate = "${income.day}/${income.month}/${income.year}"
        val txtValue = income.value
        val txtName = income.name

        itemView.findViewById<TextView>(R.id.tv_txt_name).text = txtName
        itemView.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        itemView.findViewById<TextView>(R.id.tv_txt_value).text = txtValue.toString()
    }
}