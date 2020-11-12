package com.development.pega.financialcontrol.viewholder

import android.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.SavingsMoneyItemListener
import com.development.pega.financialcontrol.model.SavingsMoney

class DepositOrWithdrawViewHolder(itemView: View, private val mItemListener: SavingsMoneyItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var btnMenu: ImageView
    private var incomeId = 0
    private lateinit var mSavingsMoney: SavingsMoney

    fun bind(savingsMoney: SavingsMoney) {
        mSavingsMoney = savingsMoney
        val date = "${savingsMoney.day}/${savingsMoney.month}/${savingsMoney.year}"
        val value = AppControl.Text.convertFloatToCurrencyText(savingsMoney.money)
        val description = savingsMoney.description

        btnMenu = itemView.findViewById(R.id.img_btn_savings_item_menu)
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_date).text = date
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_value).text = value
        itemView.findViewById<TextView>(R.id.tv_txt_deposit_withdraw_description).text = description

        btnMenu.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.img_btn_savings_item_menu) {
            showItemMenu()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.edit_item -> {
                mItemListener.onEdit(mSavingsMoney.id)
                return true
            }

            R.id.delete_item -> {
                AlertDialog.Builder(itemView.context)
                    .setMessage(R.string.confirm_delete_savings_dialog_message)
                    .setPositiveButton(R.string.confirm_delete_income_dialog_positive_button) { dialog, which ->
                        mItemListener.onDelete(mSavingsMoney)
                    }
                    .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)
                    .show()
                return true
            }
            else -> return false
        }
    }

    private fun showItemMenu() {
        val popUpMenu = PopupMenu(itemView.context, btnMenu)
        popUpMenu.menuInflater.inflate(R.menu.recycler_item_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener(this)
        popUpMenu.show()
    }


}