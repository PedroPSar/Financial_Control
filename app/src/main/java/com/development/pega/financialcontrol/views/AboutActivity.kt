package com.development.pega.financialcontrol.views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = pInfo.versionName
        binding.tvTxtAppVersion.text = versionName

        binding.tvLblLicenses.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_lbl_licenses -> showLicensesDialog()
        }
    }

    private fun showLicensesDialog() {

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_licenses, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        builder.setNegativeButton(getString(R.string.understand)) { dialog, which ->  dialog?.dismiss()}

        val dialog = builder.create()
        dialog.setOnShowListener { dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK) }
        dialog.show()
    }
}