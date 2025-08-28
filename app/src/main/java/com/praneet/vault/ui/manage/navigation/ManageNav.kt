package com.praneet.vault.ui.manage.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.praneet.vault.ui.manage.AccountTypesScreen
import com.praneet.vault.ui.manage.EntriesScreen
import com.praneet.vault.ui.manage.UsersScreen

@Composable
fun ManageNav() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "users") {
        composable("users") {
            UsersScreen(
                onUserClick = { userId -> navController.navigate("accounts/$userId") }
            )
        }
        composable(
            route = "accounts/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) {
            AccountTypesScreen(
                onAccountTypeClick = { typeId -> navController.navigate("entries/$typeId") }
            )
        }
        composable(
            route = "entries/{accountTypeId}",
            arguments = listOf(navArgument("accountTypeId") { type = NavType.LongType })
        ) { backStackEntry ->
            EntriesScreen()
        }
    }
}


