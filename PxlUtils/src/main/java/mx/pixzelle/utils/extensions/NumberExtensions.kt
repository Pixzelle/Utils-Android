package mx.pixzelle.utils.extensions

import android.content.res.Resources
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.log10

val Int.toPx get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.toSp get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()
val Int.toDp get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Number.shortPrettyCount(): String? {
    val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
    val numValue = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0.0").format(
            numValue / Math.pow(
                10.0,
                (base * 3).toDouble()
            )
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun Number.trimDecimals(
    numDecimals: Int,
    locale: Locale = Locale.getDefault()
) = "%.${numDecimals}f".format(this, locale)

fun Number?.roundTo(
    decimals: Int = 2,
    decimalSeparator: Char = '.',
    groupingSeparator: Char = ',',
    roundingMode: RoundingMode? = null
): String {
    if (this == null) {
        return "0.0"
    }

    val decimalFormatSymbols = java.text.DecimalFormatSymbols(Locale.US)
    decimalFormatSymbols.decimalSeparator = decimalSeparator
    decimalFormatSymbols.groupingSeparator = groupingSeparator

    val format = if (decimals == 0) {
        "#,###"
    } else {
        val nd = "#".repeat(decimals)
        "#,##0.$nd"
    }

    val df = DecimalFormat(format, decimalFormatSymbols)
    if (roundingMode != null) {
        df.roundingMode = roundingMode
    }
    return df.format(this)
}