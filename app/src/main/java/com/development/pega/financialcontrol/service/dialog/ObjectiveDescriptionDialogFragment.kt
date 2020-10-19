package com.development.pega.financialcontrol.service.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.development.pega.financialcontrol.R

class ObjectiveDescriptionDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            builder.setView(inflater.inflate(R.layout.dialog_objective_description, null))
                .setPositiveButton(R.string.btn_confirm, DialogInterface.OnClickListener { dialog, id ->

                })

                .setNegativeButton(R.string.btn_cancel, DialogInterface.OnClickListener { dialog, id ->
                    getDialog()?.cancel()
                })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}