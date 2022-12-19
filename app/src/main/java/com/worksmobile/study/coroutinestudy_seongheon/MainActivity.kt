package com.worksmobile.study.coroutinestudy_seongheon

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.worksmobile.study.coroutinestudy_seongheon.compose.DetailScreen
import com.worksmobile.study.coroutinestudy_seongheon.compose.MainScreen
import com.worksmobile.study.coroutinestudy_seongheon.ui.theme.CoroutineStudy_SeongHeonTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutineStudy_SeongHeonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = MAIN_SCREEN_KEY) {
                        composable(MAIN_SCREEN_KEY) { MainScreen(viewModel, navController) }
                        composable(
                            "${DETAIL_SCREEN_KEY}/{${DETAIL_ITEM}}",
                            arguments = listOf(navArgument(DETAIL_ITEM) {
                                type = NavType.StringType
                            })
                        ) { entry ->
                            DetailScreen(
                                navController,
                                entry.arguments?.getString(DETAIL_ITEM) ?: return@composable
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MAIN_SCREEN_KEY = "MAIN_SCREEN_KEY"
        const val DETAIL_SCREEN_KEY = "DETAIL_SCREEN_KEY"
        const val DETAIL_ITEM = "DETAIL_ITEM"
    }
}
