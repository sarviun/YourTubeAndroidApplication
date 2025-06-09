package com.filipsarlej.yourtube.domain.model

data class AuthenticatedUser(
    val email: String,
    val name: String?,
    val avatarUrl: String?
)