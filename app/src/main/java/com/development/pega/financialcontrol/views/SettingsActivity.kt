package com.development.pega.financialcontrol.views

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.ActivitySettingsBinding
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.viewmodels.SettingsViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mSettingsViewModel: SettingsViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var mToolbar: Toolbar
    private lateinit var prefs: Prefs
    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        prefs = AppControl.getAppPrefs(this)

        setViewModel()
        setToolbarOptions()
        setListeners()
        setColorsInViewColors()
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.view_incomes_color -> showColorDialog(binding.viewIncomesColor)
            R.id.view_expenses_color -> showColorDialog(binding.viewExpensesColor)
            R.id.view_balance_color -> showColorDialog(binding.viewBalanceColor)
            R.id.view_not_required_color -> showColorDialog(binding.viewNotRequiredColor)
            R.id.view_required_color -> showColorDialog(binding.viewRequiredColor)
            R.id.view_investment_color -> showColorDialog(binding.viewInvestmentColor)
            R.id.view_expenses_installment_color -> showColorDialog(binding.viewExpensesInstallmentColor)
            R.id.view_expenses_fixed_monthly_color -> showColorDialog(binding.viewExpensesFixedMonthlyColor)
            R.id.view_incomes_installment_color -> showColorDialog(binding.viewIncomesInstallmentColor)
            R.id.view_incomes_fixed_monthly_color -> showColorDialog(binding.viewIncomesFixedMonthlyColor)
            R.id.tv_btn_default_colors -> showDefaultColorDialog()
            R.id.tv_about -> showAboutActivity()
        }
    }

    private fun setListeners() {
        binding.viewIncomesColor.setOnClickListener(this)
        binding.viewExpensesColor.setOnClickListener(this)
        binding.viewBalanceColor.setOnClickListener(this)
        binding.viewNotRequiredColor.setOnClickListener(this)
        binding.viewRequiredColor.setOnClickListener(this)
        binding.viewInvestmentColor.setOnClickListener(this)
        binding.viewExpensesInstallmentColor.setOnClickListener(this)
        binding.viewExpensesFixedMonthlyColor.setOnClickListener(this)
        binding.viewIncomesInstallmentColor.setOnClickListener(this)
        binding.viewIncomesFixedMonthlyColor.setOnClickListener(this)
        binding.tvBtnDefaultColors.setOnClickListener(this)
        binding.tvAbout.setOnClickListener(this)
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
            .setNegativeButton(this.getString(R.string.color_dialog_negative_button)) { dialog, _ -> dialog?.dismiss() }
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
        binding.viewIncomesColor.setBackgroundColor(prefs.incomesColor)
        binding.viewExpensesColor.setBackgroundColor(prefs.expensesColor)
        binding.viewBalanceColor.setBackgroundColor(prefs.balanceColor)
        binding.viewNotRequiredColor.setBackgroundColor(prefs.notRequiredColor)
        binding.viewRequiredColor.setBackgroundColor(prefs.requiredColor)
        binding.viewInvestmentColor.setBackgroundColor(prefs.investmentColor)
        binding.viewExpensesInstallmentColor.setBackgroundColor(prefs.expensesInstallmentColor)
        binding.viewExpensesFixedMonthlyColor.setBackgroundColor(prefs.expensesFixedMonthlyColor)
        binding.viewIncomesInstallmentColor.setBackgroundColor(prefs.incomesInstallmentColor)
        binding.viewIncomesFixedMonthlyColor.setBackgroundColor(prefs.incomesFixedMonthlyColor)
    }

    private fun showDefaultColorDialog() {
        val builder = AlertDialog.Builder(this)
            .setMessage(R.string.confirm_set_default_colors)
            .setPositiveButton(R.string.yes) { _, _ ->
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