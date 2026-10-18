package tm.you.music.feature.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tm.you.music.core.designsystem.R
import tm.you.music.core.model.Song
import tm.you.music.core.network.MusicDataSource
import java.util.Calendar
import javax.inject.Inject

enum class TimeGreeting {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

enum class HomeCategory(@StringRes val labelRes: Int) {
    ALL(R.string.category_all),
    MUSIC(R.string.category_music),
    PODCAST(R.string.category_podcast),
    ENERGY(R.string.category_energy),
    FOCUS(R.string.category_focus),
    RELAXATION(R.string.category_relaxation)
}

data class HomeUiState(
    val timeGreeting: TimeGreeting = TimeGreeting.MORNING,
    val categories: List<HomeCategory> = HomeCategory.entries,
    val selectedCategory: HomeCategory = HomeCategory.ALL,
    val quickAccessSongs: List<Song> = emptyList(),
    val trendingSongs: List<Song> = emptyList(),
    val recommendedSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicDataSource: MusicDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(
            timeGreeting = calculateGreeting(),
            isLoading = true
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val trending = musicDataSource.getTrendingSongs()
                _uiState.value = _uiState.value.copy(
                    trendingSongs = trending,
                    recommendedSongs = trending.take(10),
                    quickAccessSongs = trending.take(6),
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun onCategorySelected(category: HomeCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    private fun calculateGreeting(): TimeGreeting {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> TimeGreeting.MORNING
            in 12..17 -> TimeGreeting.AFTERNOON
            in 18..22 -> TimeGreeting.EVENING
            else -> TimeGreeting.NIGHT
        }
    }
}
