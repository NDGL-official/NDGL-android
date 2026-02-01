package com.yapp.ndgl.data.auth.di

import com.yapp.ndgl.data.auth.token.TokenManagerImpl
import com.yapp.ndgl.data.core.token.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindTokenManager(
        impl: TokenManagerImpl,
    ): TokenManager
}
