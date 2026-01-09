package android.template.feature.launchlist.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.example.rocketreserver.LaunchListQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchListViewModel @Inject constructor(
    private val apolloClient: ApolloClient
): ViewModel() {
    private val _launchList = MutableStateFlow<LaunchListState<List<LaunchListQuery.Launch>>>(LaunchListState.isLoading)
    val launchList = _launchList.asStateFlow()

    private var cursor: String? = null
    private var hasMore = true
    private var isLoading = false

    init{
        loadLaunchList()
    }

    fun loadLaunchList() {
        viewModelScope.launch {
            if(isLoading) return@launch
            isLoading = true

            try {
                val response: ApolloResponse<LaunchListQuery.Data> = apolloClient.query(LaunchListQuery(Optional.present(cursor))).execute()
                val launches = response.data?.launches?.launches?.filterNotNull().orEmpty()

                cursor = response.data?.launches?.cursor
                hasMore = response.data?.launches?.hasMore ?: false

                val currentLaunches = (_launchList.value as? LaunchListState.Success)?.data ?: emptyList()
                _launchList.value = LaunchListState.Success(currentLaunches + launches)

                if (response.hasErrors()) {
                    Log.d("LaunchListViewModel", "Error: ${response.errors}")
                } else {
                    Log.d("LaunchListViewModel", "Success ${response.data}")
                }
            } catch (e: Exception) {
                _launchList.value = LaunchListState.Error(e.message ?: "Unknown Error")
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMore() {
        if (hasMore) {
            loadLaunchList()
        }
    }
}