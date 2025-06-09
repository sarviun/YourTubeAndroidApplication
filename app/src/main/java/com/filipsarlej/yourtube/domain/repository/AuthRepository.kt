package com.filipsarlej.yourtube.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentAccount: StateFlow<GoogleSignInAccount?>

    fun setAccount(account: GoogleSignInAccount?)
    suspend fun signOut()
    suspend fun getAccessToken(): String? // Získá token pro aktuálně přihlášený účet
}