package com.development.pega.financialcontrol.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.adapter.IncomesRecyclerViewAdapter
import com.development.pega.financialcontrol.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View
    private lateinit var tvLblMonth: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvIncomes: TextView
    private lateinit var tvExpenses: TextView

    private val mIncomesAdapter: IncomesRecyclerViewAdapter = IncomesRecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.home_fragment, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        tvLblMonth = root.findViewById(R.id.lbl_month_name)
        tvYear = root.findViewById(R.id.tv_year)
        tvIncomes = root.findViewById(R.id.txt_incomes)
        tvExpenses = root.findViewById(R.id.txt_expenses)

        adapters()
        observer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setCurrentMonth()
        viewModel.setYear()
        viewModel.setIncomesOfMonth()
        viewModel.setExpensesOfmMonth()
        viewModel.setIncomesInRecyclerView()
    }

    private fun observer() {
        viewModel.month.observe(viewLifecycleOwner, Observer {
            tvLblMonth.text = it
        })

        viewModel.year.observe(viewLifecycleOwner, Observer {
            tvYear.text = it.toString()
        })

        viewModel.incomes.observe(viewLifecycleOwner, Observer {
            tvIncomes.text = it.toString()
        })

        viewModel.expenses.observe(viewLifecycleOwner, Observer {
            tvExpenses.text = it.toString()
        })

        viewModel.recyclerViewIncomes.observe(viewLifecycleOwner, Observer {
            mIncomesAdapter.updateIncomesList(it)
        })
    }

    private fun adapters() {
        setInfoIncomesRecyclerView()
    }

    private fun setInfoIncomesRecyclerView() {
        val incomesRV = root.findViewById<RecyclerView>(R.id.rv_incomes)
        incomesRV.layoutManager = LinearLayoutManager(context)
        incomesRV.adapter = mIncomesAdapter
    }

}