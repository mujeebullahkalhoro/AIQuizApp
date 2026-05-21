package com.example.quizapp.utils

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.hashPassword(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(this.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}

fun getCurrentDateString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}

fun Float.toPercentageString(): String = "%.1f%%".format(this)

fun extractJsonFromResponse(raw: String): String {
    val trimmed = raw.trim()
    val jsonBlockPattern = Regex("```(?:json)?\\s*([\\s\\S]*?)```")
    val match = jsonBlockPattern.find(trimmed)
    if (match != null) return match.groupValues[1].trim()
    val firstBrace = trimmed.indexOf('{')
    val lastBrace = trimmed.lastIndexOf('}')
    if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
        return trimmed.substring(firstBrace, lastBrace + 1)
    }
    return trimmed
}
