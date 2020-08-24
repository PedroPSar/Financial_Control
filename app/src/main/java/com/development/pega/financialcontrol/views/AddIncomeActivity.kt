package com.development.pega.financialcontrol.views

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import com.development.pega.financialcontrol.R
import kotlinx.android.synthetic.main.activity_add_income.*
import java.util.*

class AddIncomeActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_income)

        setListeners()
        setSpinner()
    }

    override fun onClick(v: View?) {
        showDatePicker()
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        setCurrentDateInTxtDate(day, month, year)
        val datePickerDialog = setDatePicker(day, month, year)

        datePickerDialog.show()
    }

    private fun setDatePicker(day: Int, month: Int, year: Int): DatePickerDialog{
        return DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val txt = "$day/$month/$year"
            txt_date.text = txt
        }, year, month, day)
    }

    private fun setCurrentDateInTxtDate(day: Int, month: Int, year: Int) {
        val txt = "$day/$month/$year"
        txt_date.text = txt
    }

    private fun setListeners() {
        btn_change_date.setOnClickListener(this)
    }

    private fun setSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner_recurrence)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_recurrence_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

}