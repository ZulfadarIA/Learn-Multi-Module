package android.template.core.network

import android.template.core.data.TokenRepository
import android.template.core.data.TokensRepository
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Inject



class AuthorizationInterceptor @Inject constructor(
    private val TokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .apply {
                TokenRepository.getToken()?.let { token ->
                    addHeader("Authorization", token)
                }
            }
            .build()
        return chain.proceed(request)
    }
}

//class Apollo {
//    companion object {
//        fun getApiConnection(): ApolloClient {
//            val okHttpClient = OkHttpClient.Builder()
//            val apolloClient = ApolloClient.Builder()
//                .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql")
//                .build()
//            return apolloClient
//        }
//    }
//}