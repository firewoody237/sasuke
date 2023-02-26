package com.example.sasuke.integrated.common

fun String?.isNotEmpty(defaultValue: String): String {
    return when {
        this?.isNotEmpty() == true -> this
        else -> defaultValue
    }
}

fun String.isDigit(): Boolean {
    return try {
        this.toLong()
        true
    } catch (_: Exception) {
        false
    }
}

fun String.isNotDigit(): Boolean {
    return !this.isDigit()
}