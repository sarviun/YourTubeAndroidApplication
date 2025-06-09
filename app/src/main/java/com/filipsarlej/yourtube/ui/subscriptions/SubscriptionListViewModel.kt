package com.filipsarlej.yourtube.ui.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipsarlej.yourtube.domain.model.Subscription
import com.filipsarlej.yourtube.domain.repository.AuthRepository
import com.filipsarlej.yourtube.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface SubscriptionListUiState {
    object Loading : SubscriptionListUiState
    data class Success(val subscriptions: List<Subscription>) : SubscriptionListUiState
    data class Error(val message: String) : SubscriptionListUiState
    object RequiresAuthentication : SubscriptionListUiState
}

enum class SortType {
    ALPHABETICAL, RELEVANCE
}

@HiltViewModel
class SubscriptionListViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _allSubscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _sortType = MutableStateFlow(SortType.RELEVANCE)

    private val _uiState = MutableStateFlow<SubscriptionListUiState>(SubscriptionListUiState.Loading)
    val uiState: StateFlow<SubscriptionListUiState> = _uiState

    init {
        authRepository.currentAccount
            .onEach { account ->
                if (account != null) {
                    // Pokud je uživatel přihlášen, načteme odběry
                    fetchSubscriptions()
                } else {
                    // Pokud se odhlásí, vyčistíme seznam a nastavíme stav
                    _allSubscriptions.value = emptyList()
                    _uiState.value = SubscriptionListUiState.RequiresAuthentication
                }
            }
            .launchIn(viewModelScope)


        combine(_allSubscriptions, _searchQuery, _sortType) { subscriptions, query, sort ->
            val filteredList = if (query.isBlank()) {
                subscriptions
            } else {
                subscriptions.filter { it.title.contains(query, ignoreCase = true) }
            }

            val sortedList = when (sort) {
                SortType.ALPHABETICAL -> filteredList.sortedBy { it.title }
                SortType.RELEVANCE -> filteredList
            }

            // Pokud je přihlášen, ale seznam je prázdný (po filtraci nebo od začátku),
            // stále zobrazíme Success se prázdným seznamem, ne Error.
            if(authRepository.currentAccount.value != null) {
                _uiState.value = SubscriptionListUiState.Success(sortedList)
            }

        }.launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSortChanged(sortType: SortType) {
        _sortType.value = sortType
    }

    private fun fetchSubscriptions() {
        viewModelScope.launch {
            _uiState.value = SubscriptionListUiState.Loading
            try {
                val subscriptions = subscriptionRepository.getSubscriptions()
                _allSubscriptions.value = subscriptions
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SubscriptionListUiState.Error("Failed to load subscriptions.")
            }
        }
    }
}