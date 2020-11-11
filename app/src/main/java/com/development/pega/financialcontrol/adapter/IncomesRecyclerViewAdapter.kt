package com.development.pega.financialcontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.listener.IncomeItemListener
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.viewholder.IncomesViewHolder

class IncomesRecyclerViewAdapter(): RecyclerView.Adapter<IncomesViewHolder>() {

    private var incomesList: List<Income> = arrayListOf()
    private lateinit var mItemListener: IncomeItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomesViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.income_recycler_view_row, parent, false)
        return IncomesViewHolder(item, mItemListener)
    }

    override fun onBindViewHolder(holder: IncomesViewHolder, position: Int) {
        holder.bind(incomesList[position])
    }

    override fun getItemCount(): Int {
        return incomesList.count()
    }

    fun updateIncomesList(list: List<Income>) {
        incomesList = list
        notifyDataSetChanged()
    }

    fun attachListener(listener: IncomeItemListener) {
        mItemListener = listener
    }
}