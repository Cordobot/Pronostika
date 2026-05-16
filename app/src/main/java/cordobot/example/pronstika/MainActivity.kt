package cordobot.example.pronstika

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cordobot.example.pronstika.presentation.ui.WeatherScreen
import cordobot.example.pronstika.ui.theme.PronóstikaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PronóstikaTheme {
                WeatherScreen()
            }
        }
    }
}