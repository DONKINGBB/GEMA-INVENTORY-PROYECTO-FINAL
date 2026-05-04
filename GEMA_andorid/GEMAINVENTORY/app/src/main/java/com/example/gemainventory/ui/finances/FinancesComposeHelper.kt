package com.example.gemainventory.ui.finances

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.gemainventory.ui.theme.GemaTheme

fun interface OnGenerateReportListener {
    fun onGenerate(tipo: String)
}

class FinancesComposeHelper(private val composeView: ComposeView) {
    
    private var totalIncome = mutableStateOf(0.0)
    private var totalExpenses = mutableStateOf(0.0)
    private var netProfit = mutableStateOf(0.0)
    private var incomeEntries = mutableStateListOf<Float>()
    private var expenseEntries = mutableStateListOf<Float>()
    
    private var onGenerateReport: OnGenerateReportListener? = null

    fun setOnGenerateReport(listener: OnGenerateReportListener) {
        this.onGenerateReport = listener
    }

    init {
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GemaTheme {
                    FinancesView(
                        income = totalIncome.value.toFloat(),
                        expenses = totalExpenses.value.toFloat(),
                        incomeData = incomeEntries,
                        expenseData = expenseEntries,
                        onGenerateReport = { onGenerateReport?.onGenerate("TODO") }
                    )
                }
            }
        }
    }

    fun updateData(
        income: Double,
        expenses: Double,
        profit: Double,
        incomesList: List<Float>,
        expensesList: List<Float>
    ) {
        totalIncome.value = income
        totalExpenses.value = expenses
        netProfit.value = profit
        
        incomeEntries.clear()
        incomeEntries.addAll(incomesList)
        
        expenseEntries.clear()
        expenseEntries.addAll(expensesList)
    }
}
