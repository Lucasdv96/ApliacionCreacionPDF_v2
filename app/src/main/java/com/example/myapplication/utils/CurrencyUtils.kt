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

// Acepta formatos: 10000000 / 10.000.000 / 10.000.000,50 / 10000000,50
fun parseAmount(value: String): Double {
    val cleaned = value
        .replace(".", "")   // quitar separadores de miles
        .replace(",", ".")  // coma decimal → punto
        .trim()
    return cleaned.toDoubleOrNull() ?: 0.0
}
