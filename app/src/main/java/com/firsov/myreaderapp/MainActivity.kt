package com.firsov.myreaderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.firsov.myreaderapp.ui.screens.AddBookScreen
import com.firsov.myreaderapp.ui.screens.BookDetailsScreen
import com.firsov.myreaderapp.ui.screens.MainScreen
import com.firsov.myreaderapp.ui.theme.MyReaderAppTheme
import com.firsov.myreaderapp.viewmodel.MainViewModel

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
            val viewModel: MainViewModel = viewModel()

            // Обновляем список книг каждый раз при входе на экран
            LaunchedEffect(Unit) {
                viewModel.fetchBooks()
            }

            MainScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add") },
                onBookClick = { book -> navController.navigate("bookDetails/${book.id}") }
            )
        }

        composable("add") {
            AddBookScreen(onBookAdded = { navController.popBackStack() })
        }
        composable("bookDetails/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(
                bookId = bookId,
                onBookDeleted = { navController.popBackStack() }
            )
        }

    }

}
