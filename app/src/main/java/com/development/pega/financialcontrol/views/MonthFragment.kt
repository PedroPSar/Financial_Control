package com.development.pega.financialcontrol.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.viewmodels.MonthViewModel

class MonthFragment : Fragment() {

    companion object {
        fun newInstance() =
            MonthFragment()
    }

    private lateinit var viewModel: MonthViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.month_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MonthViewModel::class.java)
        // TODO: Use the ViewModel
    }

}