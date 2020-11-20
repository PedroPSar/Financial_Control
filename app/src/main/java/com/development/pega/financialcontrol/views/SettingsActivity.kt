package com.development.pega.financialcontrol.views

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setViewModel()
        setToolbarOptions()
        observers()
        setListeners()
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
    }

    private fun observers() {

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

    fun showColorDialog(view: View) {
        val colorDrawable = view.background as ColorDrawable
        val viewColor = colorDrawable.color

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
            .attachBrightnessSlideBar(false)
            .setBottomSpace(12)

        val colorPickerView = colorBuilder.colorPickerView
        colorPickerView.flagView = bubbleFlag
        colorPickerView.setInitialColor(resources.getColor(R.color.investment_color))
        colorBuilder.show()
    }

}