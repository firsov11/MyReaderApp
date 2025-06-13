package com.firsov.myreaderapp.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import com.firsov.myreaderapp.ui.screens.AddBookScreen
import com.firsov.myreaderapp.ui.screens.BookDetailsScreenModal
import com.firsov.myreaderapp.ui.screens.EditBookScreen
import com.firsov.myreaderapp.ui.screens.LoginScreen
import com.firsov.myreaderapp.ui.screens.MainScreen
import com.firsov.myreaderapp.ui.screens.SplashScreen
import com.firsov.myreaderapp.viewmodel.MainViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            MainScreen(
                onAddClick = { navController.navigate("addBook") },
                onBookClick = { book ->
                    navController.navigate("bookDetails/${book.id}") // ✔ правильный маршрут
                },
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }


        composable("addBook") {
            AddBookScreen(
                onBookAdded = {
                    viewModel.fetchBooks() // 👉 подгружаем книги заново
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "bookDetails/{bookId}",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it / 2 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 2 },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreenModal(
                bookId = bookId,
                viewModel = viewModel,
                onDismiss = {
                    navController.popBackStack()
                },
                onEditClick = { bookId ->
                    navController.navigate("editBook/$bookId")
                }
            )
        }

        composable("editBook/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            EditBookScreen(
                bookId = bookId,
                viewModel = viewModel,
                onBookUpdated = {
                    viewModel.fetchBooks()
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}