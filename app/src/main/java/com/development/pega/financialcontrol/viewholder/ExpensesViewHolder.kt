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
import com.development.pega.financialcontrol.listener.ExpenseItemListener
import com.development.pega.financialcontrol.model.Expense
import kotlinx.android.synthetic.main.income_recycler_view_row.view.*
import java.util.*

class ExpensesViewHolder(itemView: View, private val mItemListener: ExpenseItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var btnMenu: ImageView
    private lateinit var mExpense: Expense

    fun bind(expense: Expense) {
        mExpense = expense
        val day = checkAndChangeMaximumDayOfMonth(expense.year, expense.month, expense.day)
        val txtDay = putZeroIfNumberLessTen(day)
        val txtMonth = putZeroIfNumberLessTen(expense.month)

        val txtDate = "$txtDay/$txtMonth/${expense.year}"
        val txtValue = AppControl.Text.convertFloatToCurrencyText(expense.value)
        val txtName = expense.name

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
                mItemListener.onEdit(mExpense.id)
                true
            }
            R.id.delete_item -> {
                AlertDialog.Builder(itemView.context)
                    .setMessage(R.string.confirm_delete_expense_dialog_message)
                    .setPositiveButton(R.string.confirm_delete_income_dialog_positive_button) { dialog, which ->
                        mItemListener.onDelete(mExpense)
                    }
                    .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)
                    .show()
                true
            }
            else -> false
        }
    }

    private fun showItemMenu() {
        val popUpMenu = PopupMenu(itemView.context, btnMenu)
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
}