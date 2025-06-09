package com.filipsarlej.yourtube.data.remote.dto

import com.google.gson.annotations.SerializedName

// Hlavní obálka odpovědi
data class ChannelListResponse(
    @SerializedName("items")
    val items: List<ChannelItemDto>
)

// Položka kanálu
data class ChannelItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("snippet")
    val snippet: ChannelSnippetDto
)

// Snippet s detaily kanálu
data class ChannelSnippetDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("thumbnails")
    val thumbnails: ThumbnailsDto
)