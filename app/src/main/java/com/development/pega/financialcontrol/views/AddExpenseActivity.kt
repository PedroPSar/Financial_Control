package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.viewmodels.AddExpenseViewModel
import com.development.pega.financialcontrol.viewmodels.AddIncomeViewModel
import kotlinx.android.synthetic.main.activity_add_income.*

class AddExpenseActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mViewModel: AddExpenseViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(AddExpenseViewModel::class.java)

        mViewModel.getCurrentDate()

        observers()
        setListeners()
        setSpinners()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_change_date) {
            mViewModel.showDatePickerDialog(this)
        }
    }

    private fun setListeners() {
        btn_change_date.setOnClickListener(this)
    }

    private fun setSpinners() {
        val spinnerRecurrence: Spinner = findViewById(R.id.spinner_recurrence)
        ArrayAdapter.createFromResource(this, R.array.spinner_recurrence_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRecurrence.adapter = adapter
        }

        val spinnerType: Spinner = findViewById(R.id.spinner_type)
        ArrayAdapter.createFromResource(this, R.array.spinner_type_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }
    }

    private fun observers() {
        mViewModel.currentTime.observe(this, androidx.lifecycle.Observer {
            txt_date.text = it
        })

        mViewModel.datePickerDialog.observe(this, androidx.lifecycle.Observer {
            val datePickerDialog = it
            datePickerDialog.show()
        })
    }

}