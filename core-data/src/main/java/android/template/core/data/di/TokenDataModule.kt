package android.template.core.data.di

import android.template.core.data.TokenRepository
import android.template.core.data.TokensRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindsTokenRepository(
        tokenRepository: TokensRepository
    ): TokenRepository
}

