package com.filipsarlej.yourtube.data.repository

import com.filipsarlej.yourtube.data.remote.YouTubeApiService
import com.filipsarlej.yourtube.domain.model.ChannelDetail
import com.filipsarlej.yourtube.domain.model.Subscription
import com.filipsarlej.yourtube.domain.repository.AuthRepository
import com.filipsarlej.yourtube.domain.repository.SubscriptionRepository
import javax.inject.Inject

class SubscriptionRepositoryImpl @Inject constructor(
    private val apiService: YouTubeApiService,
    private val authRepository: AuthRepository
) : SubscriptionRepository {

    override suspend fun getSubscriptions(): List<Subscription> {
        val token = authRepository.getAccessToken() ?: return emptyList()

        try {
            val response = apiService.getSubscriptions(authToken = "Bearer $token")
            return response.items.map { dto ->
                Subscription(
                    id = dto.id,
                    title = dto.snippet.title,
                    thumbnailUrl = dto.snippet.thumbnails.high.url,
                    channelId = dto.snippet.resourceId.channelId
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }


    override suspend fun getChannelDetails(channelId: String): ChannelDetail? {
        val token = authRepository.getAccessToken() ?: return null

        try {
            val response = apiService.getChannelDetails(
                authToken = "Bearer $token",
                channelId = channelId
            )
            // API vrací seznam, ale protože hledáme podle ID, bude tam jen jedna položka
            val channelDto = response.items.firstOrNull() ?: return null

            // Mapování z DTO na náš čistý doménový model
            return ChannelDetail(
                id = channelDto.id,
                title = channelDto.snippet.title,
                description = channelDto.snippet.description,
                publishedAt = channelDto.snippet.publishedAt,
                thumbnailUrl = channelDto.snippet.thumbnails.high.url
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}