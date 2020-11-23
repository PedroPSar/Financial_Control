package com.development.pega.financialcontrol.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.SavingsMoneyItemListener
import com.development.pega.financialcontrol.model.SavingsMoney

class DepositOrWithdrawViewHolder(itemView: View, private val mItemListener: SavingsMoneyItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var btnMenu: ImageView
    private lateinit var mSavingsMoney: SavingsMoney
    private val mContext = itemView.context
    private var date = ""
    private var value = ""
    private var description = ""

    fun bind(savingsMoney: SavingsMoney) {
        mSavingsMoney = savingsMoney
        val txtDay = putZeroIfNumberLessTen(savingsMoney.day)
        val txtMonth = putZeroIfNumberLessTen(savingsMoney.month)

        date = "$txtDay/$txtMonth/${savingsMoney.year}"
        value = AppControl.Text.convertFloatToCurrencyText(savingsMoney.money)
        description = savingsMoney.description

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
                val builder = AlertDialog.Builder(itemView.context)
                    .setMessage(R.string.confirm_delete_savings_dialog_message)
                    .setPositiveButton(R.string.confirm_delete_income_dialog_positive_button) { dialog, which ->
                        mItemListener.onDelete(mSavingsMoney)
                    }
                    .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)

                val deleteDialog = builder.create()
                deleteDialog.setOnShowListener { deleteDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                                 deleteDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK)}

                deleteDialog.show()
                return true
            }
            R.id.details_item -> {
                showDetailsDialog()
                true
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

    private fun putZeroIfNumberLessTen(number: Int): String {
        return if(number < 10) {
            "0$number"
        }else {
            number.toString()
        }
    }

    private fun showDetailsDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(mContext.getString(R.string.details_dialog_title))
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_details_savings, null)
        view.findViewById<TextView>(R.id.tv_txt_value).text = value
        view.findViewById<TextView>(R.id.tv_txt_date).text = date
        view.findViewById<TextView>(R.id.tv_txt_description).text = description

        builder.setView(view)
        builder.setPositiveButton(mContext.getString(R.string.txt_ok)) { dialog, which -> dialog?.dismiss() }

        val detailsDialog = builder.create()
        detailsDialog.setOnShowListener { detailsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
            Color.BLACK) }

        detailsDialog.show()
    }

}