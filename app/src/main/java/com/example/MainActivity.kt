package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.ui.JarvisMainUi
import com.example.ui.JarvisViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Active Edge-to-Edge full bleed layout support
        enableEdgeToEdge()
        
        // Initialize our master state machine coordinator
        val viewModel = ViewModelProvider(this)[JarvisViewModel::class.java]
        
        setContent {
            MyApplicationTheme {
                JarvisMainUi(viewModel = viewModel)
            }
        }
    }
}
