package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.MonthForSum
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.Data
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val months = mContext.resources.getStringArray(R.array.months_array)
    private val incomeRepository = IncomeRepository(mContext)
    private val expenseRepository = ExpenseRepository(mContext)

    private lateinit var balanceLine: LineDataSet
    private lateinit var incomesLine: LineDataSet
    private lateinit var expensesLine: LineDataSet

    private val mYearMonthsChart = MutableLiveData<LineData>()
    var yearMonthsChart: LiveData<LineData> = mYearMonthsChart

    private val mExpensesTypePieChart = MutableLiveData<PieData>()
    var expensesTypePiechart: LiveData<PieData> = mExpensesTypePieChart

    //MonthsLineChart
    fun setDataInYearMonthsLineChart() {

        createYearMonthsLines()
        var dataSets = addLinesInDataSets()
        setStyleInLines()

        val lineData = LineData(dataSets)

        mYearMonthsChart.value = lineData
    }

    private fun addLinesInDataSets(): List<ILineDataSet> {
        var dataSets = arrayListOf<ILineDataSet>()
        dataSets.add(balanceLine)
        dataSets.add(incomesLine)
        dataSets.add(expensesLine)

        return dataSets
    }

    private fun createYearMonthsLines() {

        val monthsData = getMonthsDataFromYear()

        var balanceData = arrayListOf<Entry>()
        for(i in 0..11) {
            balanceData.add(Entry(i.toFloat(), monthsData[i].balance))
        }

        var incomeData = arrayListOf<Entry>()
        for(i in 0..11) {
            incomeData.add(Entry(i.toFloat(), monthsData[i].incomes))
        }

        var expenseData = arrayListOf<Entry>()
        for(i in 0..11) {
            expenseData.add(Entry(i.toFloat(), monthsData[i].expenses))
        }

        balanceLine = LineDataSet(balanceData, mContext.getString(R.string.lbl_balance))
        incomesLine = LineDataSet(incomeData, mContext.getString(R.string.lbl_incomes))
        expensesLine = LineDataSet(expenseData, mContext.getString(R.string.lbl_expense))

    }

    private fun getMonthsDataFromYear(): List<MonthForSum> {
        val incomesFromYear = incomeRepository.getIncomesFromYear(Data.selectedYear)
        val expensesFromYear = expenseRepository.getExpensesFromYear(Data.selectedYear)
        var sumMonths = arrayListOf<MonthForSum>()
        sumMonths.add(MonthForSum(0, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(1, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(2, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(3, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(4, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(5, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(6, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(7, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(8, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(9, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(10, 0f, 0f, 0f))
        sumMonths.add(MonthForSum(11, 0f, 0f, 0f))

        for(income in incomesFromYear) {
            when(income.month) {
                1 -> sumMonths[0].incomes += income.value
                2 -> sumMonths[1].incomes += income.value
                3 -> sumMonths[2].incomes += income.value
                4 -> sumMonths[3].incomes += income.value
                5 -> sumMonths[4].incomes += income.value
                6 -> sumMonths[5].incomes += income.value
                7 -> sumMonths[6].incomes += income.value
                8 -> sumMonths[7].incomes += income.value
                9 -> sumMonths[8].incomes += income.value
                10 -> sumMonths[9].incomes += income.value
                11 -> sumMonths[10].incomes += income.value
                12 -> sumMonths[11].incomes += income.value
                else -> {

                }
            }
        }

        for(expense in expensesFromYear) {
            when(expense.month) {
                1 -> sumMonths[0].expenses += expense.value
                2 -> sumMonths[1].expenses += expense.value
                3 -> sumMonths[2].expenses += expense.value
                4 -> sumMonths[3].expenses += expense.value
                5 -> sumMonths[4].expenses += expense.value
                6 -> sumMonths[5].expenses += expense.value
                7 -> sumMonths[6].expenses += expense.value
                8 -> sumMonths[7].expenses += expense.value
                9 -> sumMonths[8].expenses += expense.value
                10 -> sumMonths[9].expenses += expense.value
                11 -> sumMonths[10].expenses += expense.value
                12 -> sumMonths[11].expenses += expense.value
                else -> {

                }
            }
        }
        for(i in 0..11) {
            sumMonths[i].balance = sumMonths[i].incomes - sumMonths[i].expenses
        }

        return sumMonths
    }

    private fun setStyleInLines() {
        balanceLine.axisDependency = YAxis.AxisDependency.LEFT
        incomesLine.axisDependency = YAxis.AxisDependency.LEFT
        expensesLine.axisDependency = YAxis.AxisDependency.LEFT

        balanceLine.lineWidth = Constants.LINE.LINE_WIDTH
        incomesLine.lineWidth = Constants.LINE.LINE_WIDTH
        expensesLine.lineWidth = Constants.LINE.LINE_WIDTH

        balanceLine.valueTextSize = Constants.LINE.LINE_TEXT_SIZE
        incomesLine.valueTextSize = Constants.LINE.LINE_TEXT_SIZE
        expensesLine.valueTextSize = Constants.LINE.LINE_TEXT_SIZE

        setColorInLines()
    }

    private fun setColorInLines() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val balanceColor = mContext.getColor(R.color.colorPrimary)
            val incomeColor = mContext.getColor(R.color.colorIncomes)
            val expenseColor = mContext.getColor(R.color.colorExpenses)

            val balanceCircleColor = arrayListOf(balanceColor)
            val incomeCircleColor = arrayListOf(incomeColor)
            val expenseCircleColor = arrayListOf(expenseColor)

            balanceLine.color = balanceColor
            balanceLine.circleColors = balanceCircleColor
            balanceLine.valueTextColor = balanceColor

            incomesLine.color = incomeColor
            incomesLine.circleColors = incomeCircleColor
            incomesLine.valueTextColor = incomeColor

            expensesLine.color = expenseColor
            expensesLine.circleColors = expenseCircleColor
            expensesLine.valueTextColor = expenseColor
        }else {
            balanceLine.color = mContext.resources.getColor(R.color.colorPrimary)
            balanceLine.color = mContext.resources.getColor(R.color.colorIncomes)
            balanceLine.color = mContext.resources.getColor(R.color.colorExpenses)
        }
    }

    //ExpensesTypePieChart
    fun setExpensesTypePieChartData() {
        mExpensesTypePieChart.value = createMonthExpensesTypePieChartData()
    }

    private fun createMonthExpensesTypePieChartData(): PieData {
        val typeNames = mContext.resources.getStringArray(R.array.spinner_type_options)
        val colors = arrayListOf(Color.GREEN, Color.BLUE, Color.YELLOW)

        var expensesType = arrayListOf<PieEntry>()
        expensesType.add(PieEntry(500f, typeNames[0]))
        expensesType.add(PieEntry(300f, typeNames[1]))
        expensesType.add(PieEntry(450f, typeNames[2]))

        val pieDataSet = PieDataSet(expensesType, mContext.getString(R.string.expenses_type_pie_chart_name))
        pieDataSet.colors = colors

        return PieData(pieDataSet)
    }
}
