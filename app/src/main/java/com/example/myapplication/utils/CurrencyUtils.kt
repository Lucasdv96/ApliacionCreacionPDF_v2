package com.example.myapplication.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

private val arSymbols = DecimalFormatSymbols().apply {
    groupingSeparator = '.'
    decimalSeparator = ','
}

private val currencyFormat = DecimalFormat("#,##0.00", arSymbols)

fun formatCurrency(amount: Double): String = "$${currencyFormat.format(amount)}"

fun Double.toInputString(): String =
    if (this == 0.0) "" else BigDecimal(this).stripTrailingZeros().toPlainString()
