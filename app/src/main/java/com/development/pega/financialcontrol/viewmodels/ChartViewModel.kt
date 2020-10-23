package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.model.Expense
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

    private val mSelectedMonthTextView = MutableLiveData<String>()
    var selectedMonthTextView: LiveData<String> = mSelectedMonthTextView

    private val mYearMonthsChart = MutableLiveData<LineData>()
    var yearMonthsChart: LiveData<LineData> = mYearMonthsChart

    private val mExpensesTypePieChart = MutableLiveData<PieData>()
    var expensesTypePieChart: LiveData<PieData> = mExpensesTypePieChart

    private val mExpensesRecurrencePieChart = MutableLiveData<PieData>()
    var expensesRecurrencePieChart: LiveData<PieData> = mExpensesRecurrencePieChart

    private val mIncomesRecurrencePieChart = MutableLiveData<PieData>()
    var incomesRecurrencePieChart: LiveData<PieData> = mIncomesRecurrencePieChart

    fun setSelectedMonthTextView() {
        mSelectedMonthTextView.value = months[Data.selectedMonth]
    }

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

        balanceLine.mode = LineDataSet.Mode.CUBIC_BEZIER
        incomesLine.mode = LineDataSet.Mode.CUBIC_BEZIER
        expensesLine.mode = LineDataSet.Mode.CUBIC_BEZIER

        balanceLine.setDrawFilled(true)
        incomesLine.setDrawFilled(true)
        expensesLine.setDrawFilled(true)

        setColorInLines()
    }

    private fun setColorInLines() {
        var balanceColor: Int
        var incomeColor: Int
        var expenseColor: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            balanceColor = mContext.getColor(R.color.colorPrimary)
            incomeColor = mContext.getColor(R.color.colorIncomes)
            expenseColor = mContext.getColor(R.color.colorExpenses)
        }else {
            balanceColor = mContext.resources.getColor(R.color.colorPrimary)
            incomeColor = mContext.resources.getColor(R.color.colorIncomes)
            expenseColor = mContext.resources.getColor(R.color.colorExpenses)
        }

        val balanceCircleColor = arrayListOf(balanceColor)
        val incomeCircleColor = arrayListOf(incomeColor)
        val expenseCircleColor = arrayListOf(expenseColor)

        balanceLine.color = balanceColor
        balanceLine.circleColors = balanceCircleColor
        balanceLine.valueTextColor = balanceColor
        balanceLine.fillColor = balanceColor

        incomesLine.color = incomeColor
        incomesLine.circleColors = incomeCircleColor
        incomesLine.valueTextColor = incomeColor
        incomesLine.fillColor = incomeColor

        expensesLine.color = expenseColor
        expensesLine.circleColors = expenseCircleColor
        expensesLine.valueTextColor = expenseColor
        expensesLine.fillColor = expenseColor
    }

    //ExpensesTypePieChart
    fun setExpensesTypePieChartData() {
        mExpensesTypePieChart.value = createMonthExpensesTypePieChartData()
    }

    private fun createMonthExpensesTypePieChartData(): PieData {
        val typeNames = mContext.resources.getStringArray(R.array.spinner_type_options)
        var notRequiredColor: Int
        var requiredColor: Int
        var investmentColor: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notRequiredColor = mContext.getColor(R.color.not_required_color)
            requiredColor = mContext.getColor(R.color.required_color)
            investmentColor = mContext.getColor(R.color.investment_color)
        }else {
            notRequiredColor = mContext.resources.getColor(R.color.not_required_color)
            requiredColor = mContext.resources.getColor(R.color.required_color)
            investmentColor = mContext.resources.getColor(R.color.investment_color)
        }
        val colors = arrayListOf(notRequiredColor, requiredColor, investmentColor)

        //0 = not required; 1 = required; 2 = investment
        val expensesSeparateType = getExpensesTypeInYearAndMonth()

        var expensesType = arrayListOf<PieEntry>()
        expensesType.add(PieEntry(expensesSeparateType[0], typeNames[0]))
        expensesType.add(PieEntry(expensesSeparateType[1], typeNames[1]))
        expensesType.add(PieEntry(expensesSeparateType[2], typeNames[2]))

        val pieDataSet = PieDataSet(expensesType, mContext.getString(R.string.expenses_type_pie_chart_name))
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = Constants.PIE.PIE_VALUE_TEXT_SIZE

        return PieData(pieDataSet)
    }

    private fun getExpensesTypeInYearAndMonth(): List<Float> {
        val expenses = expenseRepository.getExpensesFromYearAndMonth(Data.selectedYear, Data.selectedMonth)
        var notRequiredSum = 0f
        var requiredSum = 0f
        var investmentSum = 0f

        for(expense in expenses) {
            when(expense.type) {
                Constants.TYPE.NOT_REQUIRED -> notRequiredSum += expense.value
                Constants.TYPE.REQUIRED -> requiredSum += expense.value
                Constants.TYPE.INVESTMENT -> investmentSum += expense.value
            }
        }

        return arrayListOf(notRequiredSum, requiredSum, investmentSum)
    }

    //ExpensesRecurrencePieChart
    fun setExpensesRecurrencePieChartData() {
        mExpensesRecurrencePieChart.value = createMonthExpensesRecurrencePieChartData()
    }

    private fun createMonthExpensesRecurrencePieChartData(): PieData {
        val typeNames = mContext.resources.getStringArray(R.array.spinner_recurrence_options)

        var noneColor: Int
        var installmentColor: Int
        var fixedMonthlyColor: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            noneColor = mContext.getColor(R.color.none_color)
            installmentColor = mContext.getColor(R.color.installment_color)
            fixedMonthlyColor = mContext.getColor(R.color.fixed_monthly_color)
        }else {
            noneColor = mContext.resources.getColor(R.color.none_color)
            installmentColor = mContext.resources.getColor(R.color.installment_color)
            fixedMonthlyColor = mContext.resources.getColor(R.color.fixed_monthly_color)
        }

        val colors = arrayListOf(noneColor, installmentColor, fixedMonthlyColor)

        //0 = not required; 1 = required; 2 = investment
        val expensesSeparateRecurrence = getExpensesRecurrenceInYearAndMonth()

        var expensesRecurrence = arrayListOf<PieEntry>()
        expensesRecurrence.add(PieEntry(expensesSeparateRecurrence[0], typeNames[0]))
        expensesRecurrence.add(PieEntry(expensesSeparateRecurrence[1], typeNames[1]))
        expensesRecurrence.add(PieEntry(expensesSeparateRecurrence[2], typeNames[2]))

        val pieDataSet = PieDataSet(expensesRecurrence, mContext.getString(R.string.lbl_expenses_recurrence_month_pizza_chart))
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = Constants.PIE.PIE_VALUE_TEXT_SIZE

        return PieData(pieDataSet)
    }

    private fun getExpensesRecurrenceInYearAndMonth(): List<Float> {
        val expenses = expenseRepository.getExpensesFromYearAndMonth(Data.selectedYear, Data.selectedMonth)
        var noneSum = 0f
        var installmentSum = 0f
        var fixedMonthlySum = 0f

        for(expense in expenses) {
            when(expense.recurrence) {
                Constants.RECURRENCE.NONE -> noneSum += expense.value
                Constants.RECURRENCE.INSTALLMENT -> installmentSum += expense.value
                Constants.RECURRENCE.FIXED_MONTHLY -> fixedMonthlySum += expense.value
            }
        }

        return arrayListOf(noneSum, installmentSum, fixedMonthlySum)
    }

    //IncomesRecurrencePieChart
    fun setIncomesRecurrencePieChartData() {
        mIncomesRecurrencePieChart.value = createMonthIncomesRecurrencePieChartData()
    }

    private fun createMonthIncomesRecurrencePieChartData(): PieData {
        val typeNames = mContext.resources.getStringArray(R.array.spinner_recurrence_options)

        var noneColor: Int
        var installmentColor: Int
        var fixedMonthlyColor: Int

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            noneColor = mContext.getColor(R.color.none_color)
            installmentColor = mContext.getColor(R.color.installment_color)
            fixedMonthlyColor = mContext.getColor(R.color.fixed_monthly_color)
        }else {
            noneColor = mContext.resources.getColor(R.color.none_color)
            installmentColor = mContext.resources.getColor(R.color.installment_color)
            fixedMonthlyColor = mContext.resources.getColor(R.color.fixed_monthly_color)
        }

        val colors = arrayListOf(noneColor, installmentColor, fixedMonthlyColor)

        //0 = not required; 1 = required; 2 = investment
        val incomesSeparateType = getIncomesRecurrenceInYearAndMonth()

        var incomesRecurrence = arrayListOf<PieEntry>()
        incomesRecurrence.add(PieEntry(incomesSeparateType[0], typeNames[0]))
        incomesRecurrence.add(PieEntry(incomesSeparateType[1], typeNames[1]))
        incomesRecurrence.add(PieEntry(incomesSeparateType[2], typeNames[2]))

        val pieDataSet = PieDataSet(incomesRecurrence, mContext.getString(R.string.lbl_expenses_recurrence_month_pizza_chart))
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = Constants.PIE.PIE_VALUE_TEXT_SIZE

        return PieData(pieDataSet)
    }

    private fun getIncomesRecurrenceInYearAndMonth(): List<Float> {
        val incomes = incomeRepository.getIncomesFromYearAndMonth(Data.selectedYear, Data.selectedMonth)
        var noneSum = 0f
        var installmentSum = 0f
        var fixedMonthlySum = 0f

        for(income in incomes) {
            when(income.recurrence) {
                Constants.RECURRENCE.NONE -> noneSum += income.value
                Constants.RECURRENCE.INSTALLMENT -> installmentSum += income.value
                Constants.RECURRENCE.FIXED_MONTHLY -> fixedMonthlySum += income.value
            }
        }

        return arrayListOf(noneSum, installmentSum, fixedMonthlySum)
    }
}
