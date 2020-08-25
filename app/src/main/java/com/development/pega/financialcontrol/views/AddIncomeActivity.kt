package com.development.pega.financialcontrol.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.appControl
import com.development.pega.financialcontrol.model.Income
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.viewmodels.AddIncomeViewModel
import kotlinx.android.synthetic.main.activity_add_income.*

class AddIncomeActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var mViewModel: AddIncomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private var recurrenceOption = Constants.RECURRENCE.NONE

    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income)

        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(AddIncomeViewModel::class.java)

        mViewModel.getCurrentDate()

        observers()
        setSpinner()
        setListeners()
    }

    override fun onClick(v: View) {
        if(v.id == R.id.btn_change_date) {
            mViewModel.showDatePickerDialog(this)
        } else if(v.id == R.id.btn_add) {
            val mIncome = Income()

            mIncome.description = edit_description.text.toString()
            mIncome.value = edit_value.text.toString().toFloat()
            mIncome.date = txt_date.text.toString()
            mIncome.recurrence = recurrenceOption

            mViewModel.saveIncome(mIncome)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        recurrenceOption = appControl.getRecurrence(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun setListeners() {
        btn_change_date.setOnClickListener(this)
        btn_add.setOnClickListener(this)
        spinner.onItemSelectedListener = this
    }

    private fun setSpinner() {
        spinner = findViewById(R.id.spinner_recurrence)
        ArrayAdapter.createFromResource(this, R.array.spinner_recurrence_options, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
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

        mViewModel.addIncome.observe(this, Observer {
            if(it) {
                appControl.showToast(this, getString(R.string.save_success_message))
            } else {
                appControl.showToast(this, getString(R.string.save_failed_message))
            }
        })
    }

}