package com.ecocoins.campus.utils

import java.text.SimpleDateFormat
import java.util.*

// Date Extensions
fun String.toFormattedDate(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = parser.parse(this)
        date?.let { formatter.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

fun String.toShortDate(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = parser.parse(this)
        date?.let { formatter.format(it) } ?: this
    } catch (e: Exception) {
        this
    }
}

// Number Extensions
fun Double.toFormattedString(): String = "%.2f".format(this)

fun Long.toEcoCoinsString(): String = "$this EcoCoins"

// String Extensions
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}