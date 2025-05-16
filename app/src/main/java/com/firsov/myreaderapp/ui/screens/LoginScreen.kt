package com.firsov.myreaderapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val auth = Firebase.auth
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // 👈 задаём фон!
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                    errorMessage.value = null
                },
                label = { Text("Email") },
                isError = errorMessage.value != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                    errorMessage.value = null
                },
                label = { Text("Пароль") },
                isError = errorMessage.value != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            errorMessage.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (emailState.value.isBlank() || passwordState.value.isBlank()) {
                    errorMessage.value = "Будь ласка, заповніть всі поля"
                } else {
                    signUp(auth, emailState.value, passwordState.value, onLoginSuccess, errorMessage)
                }
            }) {
                Text(text = "Зареєструватися")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                if (emailState.value.isBlank() || passwordState.value.isBlank()) {
                    errorMessage.value = "Введіть email та пароль"
                } else {
                    signIn(auth, emailState.value, passwordState.value, onLoginSuccess, errorMessage)
                }
            }) {
                Text(text = "Увійти")
            }
        }
    }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    errorMessage: MutableState<String?>
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("MyLog", "Sign up successful!")
                onSuccess()
            } else {
                errorMessage.value = it.exception?.localizedMessage ?: "Помилка реєстрації"
                Log.d("MyLog", "Sign up failure: ${errorMessage.value}")
            }
        }
}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    errorMessage: MutableState<String?>
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("MyLog", "Sign in successful!")
                onSuccess()
            } else {
                errorMessage.value = it.exception?.localizedMessage ?: "Не вдалося увійти"
                Log.d("MyLog", "Sign in failure: ${errorMessage.value}")
            }
        }
}




