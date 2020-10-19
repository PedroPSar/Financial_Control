package com.development.pega.financialcontrol.service.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.development.pega.financialcontrol.R
import kotlinx.android.synthetic.main.dialog_objective_value.*

class ObjectiveValueDialogFragment: DialogFragment() {

    internal lateinit var listener: ObjectiveValueDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.dialog_objective_value, null)

            builder.setView(view)
                .setPositiveButton(R.string.btn_confirm, DialogInterface.OnClickListener { dialog, id ->
                    listener.onObjValueDialogPositiveClick(this, edit_objective_value.text.toString())
                })

                .setNegativeButton(R.string.btn_cancel, DialogInterface.OnClickListener { dialog, id ->
                    getDialog()?.cancel()
                })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as ObjectiveValueDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement ObjectiveValueDialogListener"))
        }
    }

    interface ObjectiveValueDialogListener {
        fun onObjValueDialogPositiveClick(dialog: DialogFragment, value: String)
    }
}