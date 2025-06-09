package com.filipsarlej.yourtube.ui.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.filipsarlej.yourtube.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@Composable
fun LoginScreen(
    onLoginSuccess: (GoogleSignInAccount) -> Unit,
    onLoginFailed: () -> Unit,
    // Injectneme GoogleSignInClient přímo, abychom mohli spustit intent
    googleSignInClient: GoogleSignInClient = hiltViewModel<LoginViewModel>().googleSignInClient
) {
    // launcher, který čeká na výsledek z Google Sign-In obrazovky
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                onLoginSuccess(account)
            } catch (e: ApiException) {
                Log.w("LoginScreen", "signInResult:failed code=" + e.statusCode)
                onLoginFailed()
            }
        } else {
            onLoginFailed()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.app_intro_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = stringResource(R.string.app_intro_description)
                )
            }

            Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
                Text("Sign in with Google")
            }
        }
    }
}

// Malý ViewModel jen pro poskytnutí GoogleSignInClientu do Composable
@HiltViewModel
class LoginViewModel @Inject constructor(
    val googleSignInClient: GoogleSignInClient
) : ViewModel()