package com.development.pega.financialcontrol.viewholder

import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.SavingsMoney

class DepositOrWithdrawViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnTouchListener {

    fun bind(savingsMoney: SavingsMoney) {
        val date = "${savingsMoney.day}/${savingsMoney.month}/${savingsMoney.year}"
        val value = AppControl.Text.convertFloatToCurrencyText(savingsMoney.money)
        val description = savingsMoney.description

        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_date).text = date
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_value).text = value
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_description).text = description

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