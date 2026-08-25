package com.dailypoem.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dailypoem.app.ui.DailyPoemApp
import com.dailypoem.app.ui.theme.DailyPoemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyPoemTheme {
                DailyPoemApp()
            }
        }
    }
}
