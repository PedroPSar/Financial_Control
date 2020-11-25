package com.development.pega.financialcontrol.views

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.viewmodels.SettingsViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mSettingsViewModel: SettingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var mToolbar: Toolbar
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = AppControl.getAppPrefs(this)

        setViewModel()
        setToolbarOptions()
        setListeners()
        setColorsInViewColors()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.view_incomes_color -> showColorDialog(view_incomes_color)
            R.id.view_expenses_color -> showColorDialog(view_expenses_color)
            R.id.view_balance_color -> showColorDialog(view_balance_color)
            R.id.view_not_required_color -> showColorDialog(view_not_required_color)
            R.id.view_required_color -> showColorDialog(view_required_color)
            R.id.view_investment_color -> showColorDialog(view_investment_color)
            R.id.view_expenses_installment_color -> showColorDialog(view_expenses_installment_color)
            R.id.view_expenses_fixed_monthly_color -> showColorDialog(view_expenses_fixed_monthly_color)
            R.id.view_incomes_installment_color -> showColorDialog(view_incomes_installment_color)
            R.id.view_incomes_fixed_monthly_color -> showColorDialog(view_incomes_fixed_monthly_color)
            R.id.tv_btn_default_colors -> showDefaultColorDialog()
            R.id.tv_about -> showAboutActivity()
        }
    }

    private fun setListeners() {
        view_incomes_color.setOnClickListener(this)
        view_expenses_color.setOnClickListener(this)
        view_balance_color.setOnClickListener(this)
        view_not_required_color.setOnClickListener(this)
        view_required_color.setOnClickListener(this)
        view_investment_color.setOnClickListener(this)
        view_expenses_installment_color.setOnClickListener(this)
        view_expenses_fixed_monthly_color.setOnClickListener(this)
        view_incomes_installment_color.setOnClickListener(this)
        view_incomes_fixed_monthly_color.setOnClickListener(this)
        tv_btn_default_colors.setOnClickListener(this)
        tv_about.setOnClickListener(this)
    }

    private fun setViewModel() {
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        mSettingsViewModel = ViewModelProvider(this, mViewModelFactory).get(SettingsViewModel::class.java)
    }

    private fun setToolbarOptions() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)

        mToolbar.setNavigationOnClickListener{ onBackPressed() }
    }

    private fun showColorDialog(view: View) {
        val colorDrawable = view.background as ColorDrawable

        val bubbleFlag = BubbleFlag(this)
        bubbleFlag.flagMode = FlagMode.ALWAYS

        val colorBuilder = ColorPickerDialog.Builder(this)
            .setTitle(getString(R.string.color_dialog_title))
            .setPreferenceName(getString(R.string.color_dialog_title))
            .setPositiveButton(this.getString(R.string.color_dialog_positive_button), object:
                ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    if (envelope != null) {
                        view.setBackgroundColor(envelope.color)
                        setSelectedColorInPrefs(envelope.color, view)
                    }
                }
            })
            .setNegativeButton(this.getString(R.string.color_dialog_negative_button), object:
                DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })
            .attachAlphaSlideBar(false)
            .attachBrightnessSlideBar(true)
            .setBottomSpace(12)

        val colorPickerView = colorBuilder.colorPickerView
        colorPickerView.flagView = bubbleFlag
        colorBuilder.show()
    }

    private fun setSelectedColorInPrefs(selectedColor: Int, v: View) {
        when(v.id) {
            R.id.view_incomes_color -> prefs.incomesColor = selectedColor
            R.id.view_expenses_color -> prefs.expensesColor = selectedColor
            R.id.view_balance_color -> prefs.balanceColor = selectedColor
            R.id.view_not_required_color -> prefs.notRequiredColor = selectedColor
            R.id.view_required_color -> prefs.requiredColor = selectedColor
            R.id.view_investment_color -> prefs.investmentColor = selectedColor
            R.id.view_expenses_installment_color -> prefs.expensesInstallmentColor = selectedColor
            R.id.view_expenses_fixed_monthly_color -> prefs.expensesFixedMonthlyColor = selectedColor
            R.id.view_incomes_installment_color -> prefs.incomesInstallmentColor = selectedColor
            R.id.view_incomes_fixed_monthly_color -> prefs.incomesFixedMonthlyColor = selectedColor
        }
    }

    private fun setColorsInViewColors() {
        view_incomes_color.setBackgroundColor(prefs.incomesColor)
        view_expenses_color.setBackgroundColor(prefs.expensesColor)
        view_balance_color.setBackgroundColor(prefs.balanceColor)
        view_not_required_color.setBackgroundColor(prefs.notRequiredColor)
        view_required_color.setBackgroundColor(prefs.requiredColor)
        view_investment_color.setBackgroundColor(prefs.investmentColor)
        view_expenses_installment_color.setBackgroundColor(prefs.expensesInstallmentColor)
        view_expenses_fixed_monthly_color.setBackgroundColor(prefs.expensesFixedMonthlyColor)
        view_incomes_installment_color.setBackgroundColor(prefs.incomesInstallmentColor)
        view_incomes_fixed_monthly_color.setBackgroundColor(prefs.incomesFixedMonthlyColor)
    }

    private fun showDefaultColorDialog() {
        val builder = AlertDialog.Builder(this)
            .setMessage(R.string.confirm_set_default_colors)
            .setPositiveButton(R.string.yes) { dialog, which ->
                setDefaultColors()
            }
            .setNeutralButton(R.string.confirm_delete_income_dialog_neutral_button, null)

        val defaultColorsDialog = builder.create()
        defaultColorsDialog.setOnShowListener { defaultColorsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                                                defaultColorsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK) }

        defaultColorsDialog.show()
    }

    private fun setDefaultColors() {
        prefs.incomesColor = getResourceColor(R.color.colorIncomes)
        prefs.expensesColor = getResourceColor(R.color.colorExpenses)
        prefs.balanceColor = getResourceColor(R.color.colorBalance)
        prefs.notRequiredColor = getResourceColor(R.color.not_required_color)
        prefs.requiredColor = getResourceColor(R.color.required_color)
        prefs.investmentColor = getResourceColor(R.color.investment_color)
        prefs.expensesInstallmentColor = getResourceColor(R.color.expense_installment_color)
        prefs.expensesFixedMonthlyColor = getResourceColor(R.color.expense_fixed_monthly_color)
        prefs.incomesInstallmentColor = getResourceColor(R.color.income_installment_color)
        prefs.incomesFixedMonthlyColor = getResourceColor(R.color.income_fixed_monthly_color)

        setColorsInViewColors()
    }

    private fun getResourceColor(resColor: Int): Int {
        return if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            this.getColor(resColor)
        } else {
            this.resources.getColor(resColor)
        }
    }

    private fun showAboutActivity() {
        startActivity(Intent(this, AboutActivity::class.java))
    }

}