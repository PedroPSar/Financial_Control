package com.development.pega.financialcontrol.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.SavingsMoney
import com.development.pega.financialcontrol.viewholder.DepositOrWithdrawViewHolder

class DepositOrWithdrawRecyclerViewAdapter: RecyclerView.Adapter<DepositOrWithdrawViewHolder>() {

    private var savingsMoneyList: List<SavingsMoney> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositOrWithdrawViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(R.layout.deposit_or_withdraw_recycler_view_row, parent, false)
        return DepositOrWithdrawViewHolder(item)
    }

    override fun onBindViewHolder(holder: DepositOrWithdrawViewHolder, position: Int) {
        holder.bind(savingsMoneyList[position])
    }

    override fun getItemCount(): Int {
        return savingsMoneyList.count()
    }

    fun updateSavingsMoneyList(list: List<SavingsMoney>) {
        savingsMoneyList = list
        notifyDataSetChanged()
    }
}