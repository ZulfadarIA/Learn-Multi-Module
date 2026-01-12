package android.template.feature.launchdetail.ui

data class BookState(
    val loading: Boolean = false,
    val isBooked: Boolean = false,
    val requireLogin: Boolean = false,
    val error: String? = null
)
