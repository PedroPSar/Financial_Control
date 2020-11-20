package com.development.pega.financialcontrol.viewholder

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.listener.IncomeItemListener
import com.development.pega.financialcontrol.model.Income
import kotlinx.android.synthetic.main.income_recycler_view_row.view.*
import java.util.*

class IncomesViewHolder(itemView: View, private val mItemListener: IncomeItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    lateinit var btnMenu: ImageView
    private lateinit var mIncome: Income
    private val mContext = itemView.context

    fun bind(income: Income) {
        mIncome = income
        val day = checkAndChangeMaximumDayOfMonth(income.year, income.month, income.day)
        val txtDay = putZeroIfNumberLessTen(day)
        var txtMonth = putZeroIfNumberLessTen(income.month)

        val txtDate = "$txtDay/$txtMonth/${income.year}"
        val txtValue = AppControl.Text.convertFloatToCurrencyText(income.value)
        val txtName = income.name

        btnMenu = itemView.findViewById(R.id.img_btn_item_menu)
        itemView.findViewById<TextView>(R.id.tv_txt_name).text = txtName
        itemView.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        itemView.findViewById<TextView>(R.id.tv_txt_value).text = txtValue

        itemView.img_btn_item_menu.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.img_btn_item_menu) {
            showItemMenu()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.edit_item -> {
                mItemListener.onEdit(mIncome.id)
                true
            }
            R.id.delete_item -> {
                AlertDialog.Builder(mContext)
                    .setMessage(R.string.confirm_delete_income_dialog_message)
                    .setPositiveButton(R.string.confirm_delete_income_dialog_positive_button) { dialog, which ->
                        mItemListener.onDelete(mIncome)
                    }
                    .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)
                    .show()
                true
            }
            R.id.details_item -> {
                showDetailsDialog()
                true
            }
            else -> false
        }
    }

    private fun showItemMenu() {
        val popUpMenu = PopupMenu(mContext, btnMenu)
        popUpMenu.menuInflater.inflate(R.menu.recycler_item_menu, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener(this)
        popUpMenu.show()
    }

    private fun checkAndChangeMaximumDayOfMonth(year: Int, month: Int, day: Int): Int {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month - 1) //In calendar January is 0
        val maximum = c.getActualMaximum(Calendar.DATE)

        return if(day > maximum) {
            maximum
        }else {
            day
        }
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
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_details_incomes, null)

        builder.setView(view)
        builder.setPositiveButton("Ok"
        ) { dialog, which -> dialog?.dismiss() }
        builder.show()
    }

}