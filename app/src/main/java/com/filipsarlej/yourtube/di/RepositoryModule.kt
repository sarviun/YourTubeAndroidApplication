package com.filipsarlej.yourtube.di

import com.filipsarlej.yourtube.data.repository.AuthRepositoryImpl
import com.filipsarlej.yourtube.data.repository.SubscriptionRepositoryImpl
import com.filipsarlej.yourtube.domain.repository.AuthRepository
import com.filipsarlej.yourtube.domain.repository.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(
        subscriptionRepositoryImpl: SubscriptionRepositoryImpl
    ): SubscriptionRepository
}