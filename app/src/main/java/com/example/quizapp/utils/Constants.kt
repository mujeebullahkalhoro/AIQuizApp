package com.example.quizapp.utils

import com.example.quizapp.BuildConfig

object Constants {
    // Groq API - key is stored in local.properties and injected via BuildConfig
    val OPENAI_API_KEY: String get() = BuildConfig.GROQ_API_KEY
    const val OPENAI_BASE_URL = "https://api.groq.com/openai/v1/"
    const val OPENAI_MODEL = "llama-3.3-70b-versatile"

    const val DATABASE_NAME = "quiz_app_db"
    const val DATASTORE_NAME = "session_prefs"

    val QUIZ_TOPICS = listOf(
        "Kotlin", "Android Development", "Operating Systems", "Artificial Intelligence",
        "Database Management", "Cyber Security", "Computer Networking", "Data Structures",
        "Algorithms", "Machine Learning", "Web Development", "Cloud Computing",
        "Software Engineering", "Computer Architecture", "Python Programming"
    )

    val QUESTION_COUNT_OPTIONS = listOf(5, 10, 15, 20)
}
