package com.development.pega.financialcontrol.service.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.development.pega.financialcontrol.R

class ObjectiveValueDialogFragment: DialogFragment() {

    private lateinit var listener: ObjectiveValueDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_objective_value, null)
            val editValue = view.findViewById<EditText>(R.id.edit_objective_value)

            builder.setView(view)
                .setPositiveButton(R.string.btn_confirm, DialogInterface.OnClickListener { _, _ ->
                    listener.onObjValueDialogPositiveClick(this, editValue.text.toString())
                })

                .setNegativeButton(R.string.btn_cancel, DialogInterface.OnClickListener { _, _ ->
                    getDialog()?.cancel()
                })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun onAttach(objValueDialogListener: ObjectiveValueDialogListener) {
        listener = objValueDialogListener
    }

    interface ObjectiveValueDialogListener {
        fun onObjValueDialogPositiveClick(dialog: DialogFragment, value: String)
    }
}