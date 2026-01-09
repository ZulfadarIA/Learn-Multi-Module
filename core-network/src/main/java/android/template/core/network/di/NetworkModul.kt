package android.template.core.network.di

import android.template.core.network.apolloClient
import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NetworkModul {
    @Provides
    fun provideApolloClient(): ApolloClient {
        return apolloClient
    }
}