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
import com.development.pega.financialcontrol.listener.ExpenseItemListener
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.service.Constants
import kotlinx.android.synthetic.main.income_recycler_view_row.view.*
import java.util.*

class ExpensesViewHolder(itemView: View, private val mItemListener: ExpenseItemListener): RecyclerView.ViewHolder(itemView), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    lateinit var btnMenu: ImageView
    private lateinit var mExpense: Expense
    private val mContext = itemView.context
    private val mRecurrences = mContext.resources.getStringArray(R.array.spinner_recurrence_options)
    private val mTypes = mContext.resources.getStringArray(R.array.spinner_type_options)
    private var mExpenseRecurrence = ""
    private var txtDate = ""
    private var txtValue = ""
    private var txtName = ""

    fun bind(expense: Expense) {
        mExpense = expense
        val day = checkAndChangeMaximumDayOfMonth(expense.year, expense.month, expense.day)
        val txtDay = putZeroIfNumberLessTen(day)
        val txtMonth = putZeroIfNumberLessTen(expense.month)

        txtDate = "$txtDay/$txtMonth/${expense.year}"
        txtValue = AppControl.Text.convertFloatToCurrencyText(expense.value)
        txtName = expense.name
        mExpenseRecurrence = getExpenseRecurrence(mExpense.recurrence)

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
            R.id.details_item -> {
                showDetailsDialog()
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

    private fun showDetailsDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(mContext.getString(R.string.details_dialog_title))
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_details_expenses, null)

        val tvLblNumber = view.findViewById<TextView>(R.id.tv_lbl_number)
        val tvTxtNumber = view.findViewById<TextView>(R.id.tv_txt_number)
        val tvLblEvery = view.findViewById<TextView>(R.id.tv_lbl_every)
        val tvTxtEvery = view.findViewById<TextView>(R.id.tv_txt_every)

        view.findViewById<TextView>(R.id.tv_txt_name).text = mExpense.name
        view.findViewById<TextView>(R.id.tv_txt_type).text = getExpenseType(mExpense.type)
        view.findViewById<TextView>(R.id.tv_txt_description).text = mExpense.description
        view.findViewById<TextView>(R.id.tv_txt_value).text = txtValue
        view.findViewById<TextView>(R.id.tv_txt_date).text = txtDate
        view.findViewById<TextView>(R.id.tv_txt_recurrence).text = mExpenseRecurrence
        tvTxtNumber.text = mExpense.numInstallmentMonths.toString()
        tvTxtEvery.text = mExpense.payFrequency.toString()

        // Installment == 1
        if(mExpenseRecurrence == mRecurrences[1]) {
            tvLblNumber.visibility = View.VISIBLE
            tvTxtNumber.visibility = View.VISIBLE
            tvLblEvery.visibility = View.VISIBLE
            tvTxtEvery.visibility = View.VISIBLE
        }

        builder.setView(view)
        builder.setPositiveButton(mContext.getString(R.string.txt_ok)) { dialog, which -> dialog?.dismiss() }

        val detailsDialog = builder.create()
        detailsDialog.setOnShowListener { detailsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK) }

        detailsDialog.show()
    }

    private fun getExpenseRecurrence(recurrence: Int): String {
        return when(recurrence) {
            Constants.RECURRENCE.NONE -> mRecurrences[0]
            Constants.RECURRENCE.INSTALLMENT -> mRecurrences[1]
            Constants.RECURRENCE.FIXED_MONTHLY -> mRecurrences[2]
            else -> mRecurrences[0]
        }
    }

    private fun getExpenseType(type: Int): String {
        return when(type) {
            Constants.TYPE.NOT_REQUIRED -> mTypes[0]
            Constants.TYPE.REQUIRED -> mTypes[1]
            Constants.TYPE.INVESTMENT -> mTypes[2]
            else -> mTypes[0]
        }
    }

}