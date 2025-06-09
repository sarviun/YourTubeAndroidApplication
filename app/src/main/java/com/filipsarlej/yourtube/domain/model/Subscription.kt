package com.filipsarlej.yourtube.domain.model

data class Subscription(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val channelId: String
)