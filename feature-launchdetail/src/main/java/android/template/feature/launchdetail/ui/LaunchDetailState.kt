package android.template.feature.launchdetail.ui

sealed class LaunchDetailState <out T: Any?> {
    object isLoading : LaunchDetailState<Nothing>()
    data class Success<out T: Any>(val data: T) : LaunchDetailState<T>()
    data class Error(val errorMessage: String) : LaunchDetailState<Nothing>()
}