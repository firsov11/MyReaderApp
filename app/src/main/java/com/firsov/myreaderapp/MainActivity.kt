package com.firsov.myreaderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firsov.myreaderapp.ui.screens.AddBookScreen
import com.firsov.myreaderapp.ui.screens.MainScreen
import com.firsov.myreaderapp.ui.theme.MyReaderAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyReaderAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(onAddClick = { navController.navigate("add") })
        }
        composable("add") {
            AddBookScreen(onBookAdded = { navController.popBackStack() })
        }
    }
}
