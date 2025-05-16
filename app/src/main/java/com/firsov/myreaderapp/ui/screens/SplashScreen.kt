package com.firsov.myreaderapp.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(true) {
        delay(1000) // короткая задержка, можно убрать
        if (auth.currentUser != null) {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}
