package android.template.feature.launchlist.ui

import com.example.rocketreserver.LaunchListQuery

sealed class LaunchListState<out T: Any?> {
    object isLoading : LaunchListState<Nothing>()
    data class Success<out T: Any>(val data: T) : LaunchListState<T>()
    data class Error(val errorMessage: String) : LaunchListState<Nothing>()
}
