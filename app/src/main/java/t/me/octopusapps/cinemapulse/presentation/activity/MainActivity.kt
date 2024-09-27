package t.me.octopusapps.cinemapulse.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import t.me.octopusapps.cinemapulse.presentation.navigation.MovieApp
import t.me.octopusapps.cinemapulse.presentation.theme.CinemaPulseTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinemaPulseTheme {
                MovieApp()
            }
        }
    }
}