package com.wongtaisim.lingqian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.wongtaisim.lingqian.ui.navigation.WongTaiSimNavigation
import com.wongtaisim.lingqian.ui.theme.WongTaiSimLingQianTheme

/**
 * 主活動
 * 應用程式的入口點
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            WongTaiSimLingQianTheme {
                WongTaiSimApp()
            }
        }
    }
}

@Composable
fun WongTaiSimApp() {
    val navController = rememberNavController()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        WongTaiSimNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
