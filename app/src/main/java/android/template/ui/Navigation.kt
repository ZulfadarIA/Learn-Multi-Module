/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.ui

import android.template.feature.launchdetail.ui.LaunchDetailScreen
import android.template.feature.launchlist.ui.LaunchListScreen
import android.template.feature.login.ui.LoginScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.template.feature.mymodel.ui.MyModelScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.launchList.route) {
        composable(Screen.launchList.route) {
            LaunchListScreen(
                onLaunchClick = { launchId ->
                    navController.navigate(Screen.launchDetail.createRoute(launchId))
                }
            )
        }
        composable(
            route = Screen.launchDetail.route,
            arguments = listOf(navArgument("launchId") { type = NavType.StringType })
        ) { backstackEntry ->
            val launchId = backstackEntry.arguments!!.getString("launchId")!!
            LaunchDetailScreen(
                launchId = launchId,
                navigateToLogin = {
                    navController.navigate(Screen.login.route)
                }
            )
        }

        composable(Screen.login.route) {
            LoginScreen(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
