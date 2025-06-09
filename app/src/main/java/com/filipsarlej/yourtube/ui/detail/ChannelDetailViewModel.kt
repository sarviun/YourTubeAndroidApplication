package com.filipsarlej.yourtube.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipsarlej.yourtube.domain.model.ChannelDetail
import com.filipsarlej.yourtube.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChannelDetailUiState {
    object Loading : ChannelDetailUiState
    data class Success(val channel: ChannelDetail) : ChannelDetailUiState
    data class Error(val message: String) : ChannelDetailUiState
}

@HiltViewModel
class ChannelDetailViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    savedStateHandle: SavedStateHandle // Pro získání argumentu z navigace
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChannelDetailUiState>(ChannelDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Získáme channelId, které jsme poslali přes navigaci
        val channelId: String? = savedStateHandle.get("channelId")
        if (channelId != null) {
            fetchDetails(channelId)
        } else {
            _uiState.value = ChannelDetailUiState.Error("Channel ID not found.")
        }
    }

    private fun fetchDetails(channelId: String) {
        viewModelScope.launch {
            _uiState.value = ChannelDetailUiState.Loading
            val result = repository.getChannelDetails(channelId)
            if (result != null) {
                _uiState.value = ChannelDetailUiState.Success(result)
            } else {
                _uiState.value = ChannelDetailUiState.Error("Failed to load channel details.")
            }
        }
    }
}