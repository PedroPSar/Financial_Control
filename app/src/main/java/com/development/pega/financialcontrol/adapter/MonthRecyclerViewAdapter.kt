package com.development.pega.financialcontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Month
import com.development.pega.financialcontrol.viewholder.MonthViewHolder

class MonthRecyclerViewAdapter(): RecyclerView.Adapter<MonthViewHolder>() {

    private var monthsList: List<Month> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.month_recycler_view_row, parent, false)
        return MonthViewHolder(item)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(monthsList[position])
    }

    override fun getItemCount(): Int {
        return monthsList.count()
    }

    fun updateMonthList(list: List<Month>) {
        monthsList = list
        notifyDataSetChanged()
    }
}