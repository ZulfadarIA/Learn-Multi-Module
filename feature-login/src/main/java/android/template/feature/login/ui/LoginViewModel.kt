package android.template.feature.login.ui

import android.template.core.data.TokenRepository
import android.template.core.data.TokensRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.rocketreserver.LoginMutation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val apolloClient: ApolloClient
): ViewModel() {
    private val _loginState = MutableStateFlow<LoginState<LoginMutation.Login>>(LoginState.idle)
    val loginState = _loginState.asStateFlow()

    fun login(email: String): Boolean {
        var isLoginSucces = false

        _loginState.value = LoginState.isLoading

        viewModelScope.launch {
            try {
                val response = apolloClient.mutation(LoginMutation(email)).execute()

                when {
                    response.hasErrors() -> {
                        Log.w("Login", "Failed to login: ${response.errors?.get(0)?.message}")
                        _loginState.value = LoginState.Error(response.errors?.get(0)?.message ?: "Login failed")
                    }

                    response.data?.login?.token == null -> {
                        Log.w("Login", "Failed to Login: no token returned by the backend")
                        _loginState.value = LoginState.Error("No token returned by backend")
                    }

                    else -> {
                        val token = response.data!!.login!!.token!!
                        Log.w("Login", "Setting token: $token")
                        tokenRepository.setToken(token)

                        isLoginSucces = true
                        _loginState.value = LoginState.Success(response.data!!.login!!)
                    }
                }
            } catch (e: ApolloNetworkException) {
                _loginState.value = LoginState.Error("Please check your network connectivity.")
            } catch (e: ApolloException) {
                _loginState.value = LoginState.Error("Failed to login")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }

        return isLoginSucces
    }
}