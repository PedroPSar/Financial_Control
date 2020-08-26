package com.development.pega.financialcontrol.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.MonthRecyclerViewAdapter
import com.development.pega.financialcontrol.viewmodels.HomeViewModel
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: MonthRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        setMonthsRecyclerView()
    }

    private fun setMonthsRecyclerView() {
        rv_months.layoutManager = LinearLayoutManager(context)
        rv_months.adapter = adapter
    }

    private fun observer() {

    }

}