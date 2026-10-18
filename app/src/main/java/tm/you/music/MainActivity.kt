package tm.you.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import tm.you.music.core.designsystem.theme.YouMusicTheme
import tm.you.music.ui.YouMusicApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouMusicTheme {
                YouMusicApp()
            }
        }
    }
}
