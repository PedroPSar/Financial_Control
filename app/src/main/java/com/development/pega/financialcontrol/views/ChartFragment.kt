package com.development.pega.financialcontrol.views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.development.pega.financialcontrol.viewmodels.ChartViewModel
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.databinding.ChartFragmentBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter

class ChartFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = ChartFragment()
    }

    private lateinit var viewModel: ChartViewModel
    private lateinit var mViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    private lateinit var root: View


    private var _binding: ChartFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ChartFragmentBinding.inflate(inflater, container, false)
        root = binding.root
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireActivity().application
        mViewModelFactory = ViewModelProvider.AndroidViewModelFactory(application)
        viewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        binding.tvSelectedMonth.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        observers()
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        updateChartInfo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_selected_month -> showMonthsDialog()
        }
    }

    private fun setListeners() {
        binding.tvSelectedMonth.setOnClickListener(this)
    }

    private fun observers() {
        viewModel.selectedMonth.observe(viewLifecycleOwner, Observer {
            binding.tvSelectedMonth.text = resources.getStringArray(R.array.months_array)[it]
        })

        viewModel.yearMonthsChart.observe(viewLifecycleOwner, Observer {
            binding.lineChartYearMonths.data = it
            setStyleInYearMonthsLineChart()
            binding.lineChartYearMonths.notifyDataSetChanged()
            binding.lineChartYearMonths.invalidate()
        })

        viewModel.expensesTypePieChart.observe(viewLifecycleOwner, Observer {
            binding.pieChartMonthExpensesType.data = it
            setStyleInMonthExpensesTypePieChart()
            binding.pieChartMonthExpensesType.notifyDataSetChanged()
            binding.pieChartMonthExpensesType.invalidate()
        })

        viewModel.expensesRecurrencePieChart.observe(viewLifecycleOwner, Observer {
            binding.pieChartMonthExpensesRecurrence.data = it
            setStyleInMonthExpensesRecurrencePieChart()
            binding.pieChartMonthExpensesRecurrence.notifyDataSetChanged()
            binding.pieChartMonthExpensesRecurrence.invalidate()
        })

        viewModel.incomesRecurrencePieChart.observe(viewLifecycleOwner, Observer {
            binding.pieChartMonthIncomesRecurrence.data = it
            setStyleInMonthIncomesRecurrencePieChart()
            binding.pieChartMonthIncomesRecurrence.notifyDataSetChanged()
            binding.pieChartMonthIncomesRecurrence.invalidate()
        })
    }

    private fun setStyleInYearMonthsLineChart() {
        val description = binding.lineChartYearMonths.description
        description.isEnabled = false

        val yAxisRight = binding.lineChartYearMonths.axisRight
        yAxisRight.isEnabled = false
        yAxisRight.setDrawGridLines(false)

        val yAxisLeft = binding.lineChartYearMonths.axisLeft
        yAxisLeft.setDrawZeroLine(true)
        yAxisLeft.setDrawGridLines(false)

        val xAxis = binding.lineChartYearMonths.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(true)
        xAxis.textSize = 10f

        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        binding.lineChartYearMonths.measure(0, 0)
        xAxis.valueFormatter = MonthsNamesFormatter(context)

        binding.lineChartYearMonths.zoom(1.5f, 1f, 1f, 1f)
        binding.lineChartYearMonths.setPinchZoom(false)
        binding.lineChartYearMonths.setDrawBorders(true)
        binding.lineChartYearMonths.setDrawGridBackground(true)
        binding.lineChartYearMonths.setGridBackgroundColor(Color.WHITE)
    }

    private fun setStyleInMonthExpensesTypePieChart() {
        binding.pieChartMonthExpensesType.setEntryLabelColor(Color.BLACK)
        binding.pieChartMonthExpensesType.description.isEnabled = false
        binding.pieChartMonthExpensesType.setTouchEnabled(false)
        binding.pieChartMonthExpensesType.setDrawEntryLabels(false)

    }

    private fun setStyleInMonthExpensesRecurrencePieChart() {
        binding.pieChartMonthExpensesRecurrence.setEntryLabelColor(Color.BLACK)
        binding.pieChartMonthExpensesRecurrence.description.isEnabled = false
        binding.pieChartMonthExpensesRecurrence.setTouchEnabled(false)
        binding.pieChartMonthExpensesRecurrence.setDrawEntryLabels(false)

    }

    private fun setStyleInMonthIncomesRecurrencePieChart() {
        binding.pieChartMonthIncomesRecurrence.setEntryLabelColor(Color.BLACK)
        binding.pieChartMonthIncomesRecurrence.description.isEnabled = false
        binding.pieChartMonthIncomesRecurrence.setTouchEnabled(false)
        binding.pieChartMonthIncomesRecurrence.setDrawEntryLabels(false)

    }

    fun updateChartInfo() {
        viewModel.setSelectedMonth()
        viewModel.setDataInYearMonthsLineChart()
        viewModel.setExpensesTypePieChartData()
        viewModel.setExpensesRecurrencePieChartData()
        viewModel.setIncomesRecurrencePieChartData()

    }

    private fun showMonthsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.months_dialog_title))

        val months = resources.getStringArray(R.array.months_array)
        builder.setItems(months) { dialog, which ->
            AppControl.setSelectedMonthStartingZero(which)
            updateChartInfo()
        }

        val dialog = builder.create()
        dialog.show()
    }

    class MonthsNamesFormatter(context: Context?) : ValueFormatter() {
        private val days = context?.resources?.getStringArray(R.array.month_abbreviations)

        override fun getFormattedValue(value: Float): String {
            return AppControl.Text.convertFloatToCurrencyText(value)
        }

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return days?.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    class PieChartsValueFormatter(context: Context?) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return AppControl.Text.convertFloatToCurrencyText(value)
        }
    }

}