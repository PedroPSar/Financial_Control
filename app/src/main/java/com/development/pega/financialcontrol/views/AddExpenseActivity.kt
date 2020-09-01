package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.Expense
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.AddExpenseViewModel
import kotlinx.android.synthetic.main.activity_add_expense.*
import kotlinx.android.synthetic.main.activity_add_income.btn_add
import kotlinx.android.synthetic.main.activity_add_income.btn_change_date
import java.util.*

class AddExpenseActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mViewModel: AddExpenseViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    private lateinit var spinnerRecurrence: Spinner
    private lateinit var spinnerType: Spinner

    private var typeOptions = Constants.TYPE.NOT_REQUIRED
    private var recurrenceOptions = Constants.RECURRENCE.NONE

    private var selectedDay = 0
    private var selectedMonth = 0
    private var selectedYear = 0
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(AddExpenseViewModel::class.java)

        mViewModel.getCurrentDate()

        setSpinners()
        observers()
        setListeners()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_change_date) {
            mViewModel.showDatePickerDialog(this)
        } else if(v.id == R.id.btn_add) {
            calendar = AppControl.calendarSetTime(txt_expense_date.text.toString())
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)

            val expense = Expense()
            expense.type = typeOptions
            expense.day = day
            expense.month = month
            expense.year = year
            expense.value = edit_expense_value.text.toString().toFloat()
            expense.name = edit_expense_name.text.toString()
            expense.description = edit_expense_description.text.toString()
            expense.recurrence = recurrenceOptions

            mViewModel.addExpense(expense)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        if(view.id == R.id.spinner_type) {
            typeOptions = AppControl.getType(position)
        }else if(view.id == R.id.spinner_recurrence) {
            recurrenceOptions = AppControl.getRecurrence(position)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun setListeners() {
        btn_change_date.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        spinnerRecurrence.onItemSelectedListener = this
        spinnerType.onItemSelectedListener = this
    }

    private fun setSpinners() {
        spinnerRecurrence = findViewById(R.id.spinner_recurrence)
        ArrayAdapter.createFromResource(this, R.array.spinner_recurrence_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRecurrence.adapter = adapter
        }

        spinnerType = findViewById(R.id.spinner_type)
        ArrayAdapter.createFromResource(this, R.array.spinner_type_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }
    }

    private fun observers() {
        mViewModel.currentTime.observe(this, androidx.lifecycle.Observer {
            txt_expense_date.text = it
        })

        mViewModel.datePickerDialog.observe(this, androidx.lifecycle.Observer {
            val datePickerDialog = it
            datePickerDialog.show()
        })

        mViewModel.saveExpense.observe(this, Observer {
            if(it) {
                AppControl.showToast(this, getString(R.string.save_success_message))
                finish()
            } else {
                AppControl.showToast(this, getString(R.string.save_failed_message))
                finish()
            }
        })
    }

}