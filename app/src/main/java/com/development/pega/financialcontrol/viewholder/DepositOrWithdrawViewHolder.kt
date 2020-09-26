package com.development.pega.financialcontrol.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.SavingsMoney

class DepositOrWithdrawViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun bind(savingsMoney: SavingsMoney) {
        val date = "${savingsMoney.day}/${savingsMoney.month}/${savingsMoney.year}"
        val value = savingsMoney.money
        val description = savingsMoney.description

        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_date).text = date
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_value).text = value.toString()
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_description).text = description

    }
}