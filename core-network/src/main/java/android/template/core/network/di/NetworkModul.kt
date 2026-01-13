package android.template.core.network.di

import android.template.core.network.AuthorizationInterceptor
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModul {

    @Provides
    @Singleton
    fun provideApolloClient(authorizationInterceptor: AuthorizationInterceptor): ApolloClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authorizationInterceptor)
            .build()

        val apolloClient = ApolloClient.Builder()
            .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
            .webSocketServerUrl("wss://apollo-fullstack-tutorial.herokuapp.com/graphql")
            .webSocketReopenWhen { throwable, attempt ->
                Log.d("Apollo", "WebSocket got disconnected, reopening after a delay", throwable)
                delay(attempt * 1000)
                true
            }
            .okHttpClient(okHttpClient)
            .build()

        return apolloClient
    }
}