package com.filipsarlej.yourtube.data.remote

import com.filipsarlej.yourtube.data.remote.dto.ChannelListResponse
import com.filipsarlej.yourtube.data.remote.dto.SubscriptionListResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YouTubeApiService {

    @GET("youtube/v3/subscriptions")
    suspend fun getSubscriptions(
        // Token se posílá v hlavičce pro autorizaci
        @Header("Authorization") authToken: String,

        // Parametry dotazu
        @Query("part") part: String = "snippet,contentDetails",
        @Query("mine") mine: Boolean = true,
        @Query("maxResults") maxResults: Int = 50 // Max je 50 na stránku
    ): SubscriptionListResponse

    @GET("youtube/v3/channels")
    suspend fun getChannelDetails(
        @Header("Authorization") authToken: String,
        @Query("part") part: String = "snippet",
        @Query("id") channelId: String
    ): ChannelListResponse

}