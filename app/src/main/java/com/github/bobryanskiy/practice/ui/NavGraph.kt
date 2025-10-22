package com.github.bobryanskiy.practice.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.bobryanskiy.practice.ui.home.HomeScreen
import com.github.bobryanskiy.practice.ui.task.TaskScreen
import com.github.bobryanskiy.practice.ui.topicSelection.TopicSelectionScreen
import kotlinx.serialization.Serializable

@Serializable
object Home
@Serializable
object TopicSelection
@Serializable
data class Task(val topic: String, val difficulty: String)

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                onNavigateToTopics = {
                    navController.navigate(route = TopicSelection)
                }
            )
        }
        composable<TopicSelection> {
            TopicSelectionScreen(
                onNavigateToTask = { topic, difficulty ->
                    navController.navigate(route = Task(topic, difficulty))
                }
            )
        }
        composable<Task> {
            TaskScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

