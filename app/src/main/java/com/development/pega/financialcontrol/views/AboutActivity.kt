package com.development.pega.financialcontrol.views

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.development.pega.financialcontrol.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = pInfo.versionName
        tv_txt_app_version.text = versionName

        tv_lbl_licenses.setOnClickListener(this)
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