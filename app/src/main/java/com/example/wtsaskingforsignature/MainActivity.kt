package com.example.wtsaskingforsignature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wtsaskingforsignature.ui.screens.*
import com.example.wtsaskingforsignature.ui.theme.WtsTheme
import com.example.wtsaskingforsignature.util.WtsLogger

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val navigateTarget = intent?.getStringExtra("navigate")
		setContent {
			WtsTheme {
				Surface {
					WtsNavHost(startDestinationOverride = navigateTarget)
				}
			}
		}
	}
}

object Routes {
	const val HOME = "home"
	const val DIRECT_DRAW = "direct_draw"
	const val CUP_DRAW = "cup_draw"
	const val DAILY = "daily"
	const val BROWSE = "browse"
	const val STEPS = "steps"
	const val PREVIEW = "preview"
	const val CUP = "cup/{id}"
	const val RESULT = "result/{valid}"
	const val CONTENT = "content/{id}"
	const val CHAT = "chat/{id}"

	fun content(id: Int) = "content/$id"
	fun result(valid: Boolean) = "result/$valid"
	fun chat(id: Int) = "chat/$id"
	fun cup(id: Int) = "cup/$id"
}

@Composable
fun WtsNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController = rememberNavController(),
	startDestinationOverride: String? = null
) {
	val start = when (startDestinationOverride) {
		Routes.DIRECT_DRAW -> Routes.DIRECT_DRAW
		Routes.CUP_DRAW -> Routes.CUP_DRAW
		Routes.DAILY -> Routes.DAILY
		Routes.BROWSE -> Routes.BROWSE
		Routes.STEPS -> Routes.STEPS
		Routes.PREVIEW -> Routes.PREVIEW
		else -> Routes.HOME
	}
	NavHost(navController = navController, startDestination = start, modifier = modifier) {
		composable(Routes.HOME) { HomeScreen(navController) }
		composable(Routes.DIRECT_DRAW) { DirectDrawScreen(navController) }
		composable(Routes.CUP_DRAW) { CupDrawScreen(navController) }
		composable(Routes.DAILY) { DailyScreen(navController) }
		composable(Routes.BROWSE) { BrowseScreen(navController) }
		composable(Routes.STEPS) { StepsScreen(navController) }
		composable(Routes.PREVIEW) { PreviewScreen(navController) }
		composable(
			Routes.CUP,
			arguments = listOf(navArgument("id") { type = NavType.IntType })
		) { backStackEntry ->
			val id = backStackEntry.arguments?.getInt("id") ?: 1
			CupScreen(navController, id)
		}
		composable(
			Routes.RESULT,
			arguments = listOf(navArgument("valid") { type = NavType.BoolType })
		) { backStackEntry ->
			val valid = backStackEntry.arguments?.getBoolean("valid") ?: false
			CupResultScreen(navController, valid)
		}
		composable(
			Routes.CONTENT,
			arguments = listOf(navArgument("id") { type = NavType.StringType })
		) { backStackEntry ->
			val idStr = backStackEntry.arguments?.getString("id")
			val id = idStr?.toIntOrNull() ?: 1
			WtsLogger.i("Navigate Content id=$idStr -> parsed=$id")
			ContentScreen(navController, id)
		}
		composable(
			Routes.CHAT,
			arguments = listOf(navArgument("id") { type = NavType.IntType })
		) { backStackEntry ->
			val id = backStackEntry.arguments?.getInt("id") ?: 1
			ChatScreen(navController, id)
		}
	}
}
