package com.filipsarlej.yourtube.data.repository

import android.content.Context
import com.filipsarlej.yourtube.domain.repository.AuthRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val _currentAccount = MutableStateFlow<GoogleSignInAccount?>(null)
    override val currentAccount = _currentAccount.asStateFlow()

    init {
        _currentAccount.value = GoogleSignIn.getLastSignedInAccount(context)
    }

    override fun setAccount(account: GoogleSignInAccount?) {
        _currentAccount.value = account
    }

    override suspend fun signOut() {
        val gsc = GoogleSignIn.getClient(context, com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
        gsc.signOut().addOnCompleteListener {
            setAccount(null)
        }
    }

    override suspend fun getAccessToken(): String? {
        val account = _currentAccount.value ?: return null
        return withContext(Dispatchers.IO) {
            try {
                val scope = "https://www.googleapis.com/auth/youtube.readonly"
                GoogleAuthUtil.getToken(context, account.account!!, "oauth2:$scope")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}