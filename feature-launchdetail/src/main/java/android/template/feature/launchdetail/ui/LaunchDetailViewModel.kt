package android.template.feature.launchdetail.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.example.rocketreserver.LaunchDetailQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchDetailViewModel @Inject constructor(
    private val apolloClient: ApolloClient
): ViewModel() {
    private val _launchDetail = MutableStateFlow<LaunchDetailState<LaunchDetailQuery.Data>>(LaunchDetailState.isLoading)
    val launchDetail = _launchDetail.asStateFlow()

    fun loadLaunchDetail(launchId: String) {
        viewModelScope.launch {
            try {
                val response = apolloClient.query(LaunchDetailQuery(launchId)).execute()
                val launchDetailData = response.data!!
                Log.d("LaunchDetailViewModel", "Success ${response.data}")
                _launchDetail.value = LaunchDetailState.Success(launchDetailData)

            } catch (e: Exception) {
                _launchDetail.value = LaunchDetailState.Error(e.message ?: "Unknown Error")
                Log.d("LaunchDetailViewModel", "Error: ${e.message}")
            }
        }
    }
}