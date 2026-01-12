package android.template.ui

sealed class Screen(val route: String) {
    data object launchList: Screen("main")
    data object launchDetail: Screen("detail/{launchId}") {
        fun createRoute(launchId: String) = "detail/$launchId"
    }
    data object login: Screen("login")
}