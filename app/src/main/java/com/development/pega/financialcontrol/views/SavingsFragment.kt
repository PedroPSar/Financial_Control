package com.development.pega.financialcontrol.views

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.viewmodels.SavingsViewModel

class SavingsFragment : Fragment() {

    companion object {
        fun newInstance() = SavingsFragment()
    }

    private lateinit var viewModel: SavingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.savings_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SavingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}