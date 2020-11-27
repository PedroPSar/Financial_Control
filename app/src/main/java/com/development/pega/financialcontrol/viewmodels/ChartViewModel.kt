package com.development.pega.financialcontrol.viewmodels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.development.pega.financialcontrol.R
import com.development.pega.financialcontrol.control.AppControl
import com.development.pega.financialcontrol.model.MonthForSum
import com.development.pega.financialcontrol.service.Constants
import com.development.pega.financialcontrol.service.Data
import com.development.pega.financialcontrol.service.repository.Prefs
import com.development.pega.financialcontrol.service.repository.expense.ExpenseRepository
import com.development.pega.financialcontrol.service.repository.income.IncomeRepository
import com.development.pega.financialcontrol.views.ChartFragment
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    private val mContext = application.applicationContext
    private val months = mContext.resources.getStringArray(R.array.months_array)
    private val incomeRepository = IncomeRepository(mContext)
    private val expenseRepository = ExpenseRepository(mContext)
    private val mPrefs = AppControl.getAppPrefs(mContext)

    private lateinit var balanceLine: LineDataSet
    private lateinit var incomesLine: LineDataSet
    private lateinit var expensesLine: LineDataSet

    private val mSelectedMonth = MutableLiveData<Int>()
    var selectedMonth: LiveData<Int> = mSelectedMonth

    private val mYearMonthsChart = MutableLiveData<LineData>()
    var yearMonthsChart: LiveData<LineData> = mYearMonthsChart

    private val mExpensesTypePieChart = MutableLiveData<PieData>()
    var expensesTypePieChart: LiveData<PieData> = mExpensesTypePieChart

    private val mExpensesRecurrencePieChart = MutableLiveData<PieData>()
    var expensesRecurrencePieChart: LiveData<PieData> = mExpensesRecurrencePieChart

    private val mIncomesRecurrencePieChart = MutableLiveData<PieData>()
    var incomesRecurrencePieChart: LiveData<PieData> = mIncomesRecurrencePieChart

    fun setSelectedMonth() {
        mSelectedMonth.value = AppControl.getSelectedMonthForArrays() // -1 Correction position for spinner
    }

    //MonthsLineChart
    fun setDataInYearMonthsLineChart() {
        createYearMonthsLines()
        var dataSets = addLinesInDataSets()
        setStyleInLines()

        val lineData = LineData(dataSets)
        lineData.setValueFormatter(ChartFragment.MonthsNamesFormatter(mContext))
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
        val allIncomes = incomeRepository.getAll()
        val allExpenses = expenseRepository.getAll()
        var currentSelectedMonth = Data.selectedMonth

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

        for(i in 1..12) {
            Data.selectedMonth = i

            sumMonths[i - 1].incomes = AppControl.sumSelectedMonthIncomes(allIncomes)
            sumMonths[i - 1].expenses = AppControl.sumSelectedMonthExpenses(allExpenses)
            sumMonths[i - 1].balance = AppControl.calcAccountBalance(mContext)
        }

        Data.selectedMonth = currentSelectedMonth
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

        balanceLine.mode = LineDataSet.Mode.LINEAR
        incomesLine.mode = LineDataSet.Mode.LINEAR
        expensesLine.mode = LineDataSet.Mode.LINEAR

        balanceLine.setDrawFilled(true)
        incomesLine.setDrawFilled(true)
        expensesLine.setDrawFilled(true)

        setColorInLines()
    }

    private fun setColorInLines() {
        var balanceColor = mPrefs.balanceColor
        var incomeColor = mPrefs.incomesColor
        var expenseColor = mPrefs.expensesColor

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
        var notRequiredColor = mPrefs.notRequiredColor
        var requiredColor = mPrefs.requiredColor
        var investmentColor = mPrefs.investmentColor

        val colors = arrayListOf(notRequiredColor, requiredColor, investmentColor)

        //0 = not required; 1 = required; 2 = investment
        val expensesSeparateType = getExpensesTypeInYearAndMonthSelected()

        var expensesType = arrayListOf<PieEntry>()
        expensesType.add(PieEntry(expensesSeparateType[0], typeNames[0]))
        expensesType.add(PieEntry(expensesSeparateType[1], typeNames[1]))
        expensesType.add(PieEntry(expensesSeparateType[2], typeNames[2]))

        val pieDataSet = PieDataSet(expensesType, "")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = Constants.PIE.PIE_VALUE_TEXT_SIZE
        pieDataSet.valueFormatter = ChartFragment.PieChartsValueFormatter(mContext)

        return PieData(pieDataSet)
    }

    private fun getExpensesTypeInYearAndMonthSelected(): List<Float> {
        val expenses = expenseRepository.getAll()

        var notRequiredSum = 0f
        var requiredSum = 0f
        var investmentSum = 0f

        for(expense in expenses) {

            if(expense.month == Data.selectedMonth && expense.year == Data.selectedYear
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == Data.selectedYear && expense.month < Data.selectedMonth
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < Data.selectedYear
                || AppControl.thisInstallmentExpenseIsFromThisMonth(expense)) {

                when(expense.type) {
                    Constants.TYPE.NOT_REQUIRED -> notRequiredSum += expense.value
                    Constants.TYPE.REQUIRED -> requiredSum += expense.value
                    Constants.TYPE.INVESTMENT -> investmentSum += expense.value
                }

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
        var installmentExpensesColor = mPrefs.expensesInstallmentColor
        var fixedMonthlyExpensesColor = mPrefs.expensesFixedMonthlyColor

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            noneColor = mContext.getColor(R.color.expense_none_color)
        }else {
            noneColor = mContext.resources.getColor(R.color.expense_none_color)
        }

        val colors = arrayListOf(noneColor, installmentExpensesColor, fixedMonthlyExpensesColor)

        //0 = none; 1 = installment; 2 = fixedMonthly
        val expensesSeparateRecurrence = getExpensesRecurrenceInYearAndMonth()

        var expensesRecurrence = arrayListOf<PieEntry>()
        expensesRecurrence.add(PieEntry(expensesSeparateRecurrence[0], typeNames[0]))
        expensesRecurrence.add(PieEntry(expensesSeparateRecurrence[1], typeNames[1]))
        expensesRecurrence.add(PieEntry(expensesSeparateRecurrence[2], typeNames[2]))

        val pieDataSet = PieDataSet(expensesRecurrence, "")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = Constants.PIE.PIE_VALUE_TEXT_SIZE
        pieDataSet.valueFormatter = ChartFragment.PieChartsValueFormatter(mContext)

        return PieData(pieDataSet)
    }

    private fun getExpensesRecurrenceInYearAndMonth(): List<Float> {
        val expenses = expenseRepository.getAll()
        var noneSum = 0f
        var installmentSum = 0f
        var fixedMonthlySum = 0f

        for(expense in expenses) {

            if(expense.month == Data.selectedMonth && expense.year == Data.selectedYear
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year == Data.selectedYear && expense.month < Data.selectedMonth
                || expense.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && expense.year < Data.selectedYear
                || AppControl.thisInstallmentExpenseIsFromThisMonth(expense)) {

                when(expense.recurrence) {
                    Constants.RECURRENCE.NONE -> noneSum += expense.value
                    Constants.RECURRENCE.INSTALLMENT -> installmentSum += expense.value
                    Constants.RECURRENCE.FIXED_MONTHLY -> fixedMonthlySum += expense.value
                }

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
        var installmentIncomesColor = mPrefs.incomesInstallmentColor
        var fixedMonthlyIncomesColor = mPrefs.incomesFixedMonthlyColor

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            noneColor = mContext.getColor(R.color.income_none_color)
        }else {
            noneColor = mContext.resources.getColor(R.color.income_none_color)
        }

        val colors = arrayListOf(noneColor, installmentIncomesColor, fixedMonthlyIncomesColor)

        //0 = not required; 1 = required; 2 = investment
        val incomesSeparateType = getIncomesRecurrenceInYearAndMonth()

        var incomesRecurrence = arrayListOf<PieEntry>()
        incomesRecurrence.add(PieEntry(incomesSeparateType[0], typeNames[0]))
        incomesRecurrence.add(PieEntry(incomesSeparateType[1], typeNames[1]))
        incomesRecurrence.add(PieEntry(incomesSeparateType[2], typeNames[2]))

        val pieDataSet = PieDataSet(incomesRecurrence, "")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = Constants.PIE.PIE_VALUE_TEXT_SIZE
        pieDataSet.valueFormatter = ChartFragment.PieChartsValueFormatter(mContext)

        return PieData(pieDataSet)
    }

    private fun getIncomesRecurrenceInYearAndMonth(): List<Float> {
        val incomes = incomeRepository.getAll()
        var noneSum = 0f
        var installmentSum = 0f
        var fixedMonthlySum = 0f

        for(income in incomes) {

            if(income.month == Data.selectedMonth && income.year == Data.selectedYear
                || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year == Data.selectedYear && income.month < Data.selectedMonth
                || income.recurrence == Constants.RECURRENCE.FIXED_MONTHLY && income.year < Data.selectedYear
                || AppControl.thisInstallmentIncomeIsFromThisMonth(income)) {

                when(income.recurrence) {
                    Constants.RECURRENCE.NONE -> noneSum += income.value
                    Constants.RECURRENCE.INSTALLMENT -> installmentSum += income.value
                    Constants.RECURRENCE.FIXED_MONTHLY -> fixedMonthlySum += income.value
                }

            }

        }

        return arrayListOf(noneSum, installmentSum, fixedMonthlySum)
    }
}
