package com.dailypoem.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun DailyPoemApp(viewModel: PoemViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onPoemClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            DetailScreen(
                viewModel = viewModel,
                poemId = id,
                onBack = { navController.popBackStack() },
                onPoetClick = { author ->
                    viewModel.openPoet(author)
                    navController.navigate("poet")
                }
            )
        }
        composable("poet") {
            PoetScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onPoemClick = { id -> navController.navigate("detail/$id") }
            )
        }
    }
}
