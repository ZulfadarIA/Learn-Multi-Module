package android.template.feature.launchdetail.ui

import android.template.core.data.TokenRepository
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloNetworkException
import com.example.rocketreserver.BookTripMutation
import com.example.rocketreserver.CancelTripMutation
import com.example.rocketreserver.LaunchDetailQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val apolloClient: ApolloClient
): ViewModel() {
    private val _launchDetail = MutableStateFlow<LaunchDetailState<LaunchDetailQuery.Data>>(LaunchDetailState.isLoading)
    val launchDetail = _launchDetail.asStateFlow()

    private val _bookState = MutableStateFlow(BookState())
    val bookState = _bookState.asStateFlow()


    fun loadLaunchDetail(launchId: String) {
        viewModelScope.launch {
            try {
                val response = apolloClient.query(LaunchDetailQuery(launchId)).execute()
                if (response.hasErrors()) {
                    _launchDetail.value = LaunchDetailState.Error(
                        response.errors!!.first().message
                    )
                    return@launch
                }
                val launchDetailData = response.data
                if (launchDetailData == null) {
                    _launchDetail.value =
                        LaunchDetailState.Error("Empty response from server")
                    return@launch
                }
                _launchDetail.value = LaunchDetailState.Success(launchDetailData)
                Log.d("LaunchDetailViewModel", "Success ${response.data}")
                _bookState.value = _bookState.value.copy(
                    isBooked = launchDetailData.launch?.isBooked ?: false
                )
            } catch (e: ApolloNetworkException) {
                _launchDetail.value = LaunchDetailState.Error("Please check your network connectivity.")
                Log.d("LaunchDetailViewModel", "Error network exception: ${e.message}")
            } catch (e: ApolloException) {
                _launchDetail.value = LaunchDetailState.Error(e.message ?: "Oh no... An error happened.")
                Log.d("LaunchDetailViewModel", "Error Apollo exception: ${e.message}")
            } catch (e: Exception) {
                _launchDetail.value = LaunchDetailState.Error(e.message ?: "Oh no... An error happened.")
                Log.d("LaunchDetailViewModel", "Error exception: ${e.message}")
            }
        }
    }

    fun onBookButtonClick(launchId: String) {
        viewModelScope.launch {
            if (tokenRepository.getToken() == null) {
                _bookState.value = _bookState.value.copy(requireLogin = true)
                return@launch
            }

            _bookState.value = _bookState.value.copy(loading = true)

            val isBooked = _bookState.value.isBooked
            val mutation = if (isBooked) {
                CancelTripMutation(launchId)
            } else {
                BookTripMutation(listOf(launchId))
            }

            try {
                val response = apolloClient.mutation(mutation).execute()
                if (response.hasErrors() || response.exception != null) {
                    _bookState.value =
                        _bookState.value.copy(
                            loading = false,
                            error = response.errors?.firstOrNull()?.message
                        )
                } else {
                    _bookState.value =
                        _bookState.value.copy(
                            loading = false,
                            isBooked = !isBooked
                        )
                }
            } catch (e: Exception) {
                _bookState.value =
                    _bookState.value.copy(
                        loading = false,
                        error = e.message
                    )
            }
        }
    }

    fun consumeLoginEvent() {
        _bookState.value = _bookState.value.copy(requireLogin = false)
    }
}