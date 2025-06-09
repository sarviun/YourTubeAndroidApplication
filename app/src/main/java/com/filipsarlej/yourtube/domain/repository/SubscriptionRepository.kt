package com.filipsarlej.yourtube.domain.repository

import com.filipsarlej.yourtube.domain.model.ChannelDetail
import com.filipsarlej.yourtube.domain.model.Subscription


interface SubscriptionRepository {
    suspend fun getSubscriptions(): List<Subscription>
    suspend fun getChannelDetails(channelId: String): ChannelDetail?
}
