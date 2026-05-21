package com.example.quizapp.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object Quiz : Screen("quiz")
    data object Result : Screen("result")
    data object History : Screen("history")
    data object Profile : Screen("profile")
    data object QuizDetail : Screen("quiz_detail/{quizId}") {
        fun createRoute(quizId: Int) = "quiz_detail/$quizId"
    }
}
