package com.filipsarlej.yourtube.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipsarlej.yourtube.domain.model.AuthenticatedUser
import com.filipsarlej.yourtube.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AccountUiState(
    val user: AuthenticatedUser? = null,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState = _uiState.asStateFlow()

    init {

        authRepository.currentAccount
            .onEach { googleAccount ->
                // Zde probíhá klíčové mapování
                val authenticatedUser = googleAccount?.let {
                    AuthenticatedUser(
                        email = it.email!!,
                        name = it.displayName,
                        avatarUrl = it.photoUrl?.toString()
                    )
                }
                // Pokaždé, když se uživatel změní (přihlásí/odhlásí), aktualizujeme stav
                _uiState.value = _uiState.value.copy(user = authenticatedUser)
            }
            .launchIn(viewModelScope)
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }
    }
}