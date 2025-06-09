package com.filipsarlej.yourtube.data.remote.dto

import com.google.gson.annotations.SerializedName

// Hlavní obálka celé odpovědi z API
data class SubscriptionListResponse(
    @SerializedName("items")
    val items: List<SubscriptionItemDto>
)

// Každá položka v seznamu odběrů
data class SubscriptionItemDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("snippet")
    val snippet: SubscriptionSnippetDto
)

// "Snippet"
data class SubscriptionSnippetDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("resourceId")
    val resourceId: ResourceIdDto,
    @SerializedName("thumbnails")
    val thumbnails: ThumbnailsDto
)

// ID kanálu, který odebíráme
data class ResourceIdDto(
    @SerializedName("channelId")
    val channelId: String
)

// Objekt obsahující různé velikosti náhledových obrázků
data class ThumbnailsDto(
    @SerializedName("default")
    val default: ThumbnailDto,
    @SerializedName("medium")
    val medium: ThumbnailDto,
    @SerializedName("high")
    val high: ThumbnailDto
)

// Detail jednoho náhledového obrázku
data class ThumbnailDto(
    @SerializedName("url")
    val url: String
)