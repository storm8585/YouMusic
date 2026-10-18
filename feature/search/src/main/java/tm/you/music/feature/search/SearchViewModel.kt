package tm.you.music.feature.search

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tm.you.music.core.designsystem.R
import tm.you.music.core.model.Song
import tm.you.music.core.network.MusicDataSource
import javax.inject.Inject

enum class SearchFilter(@StringRes val labelRes: Int) {
    ALL(R.string.category_all),
    SONGS(R.string.filter_songs),
    ARTISTS(R.string.filter_artists),
    ALBUMS(R.string.filter_albums)
}

data class MusicGenre(
    val id: String,
    @StringRes val titleRes: Int,
    val searchQuery: String,
    val hexColor: Long,
    val imageUrl: String? = null
)

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val isLoadingResults: Boolean = false,
    val filters: List<SearchFilter> = SearchFilter.entries,
    val selectedFilter: SearchFilter = SearchFilter.ALL,
    val genres: List<MusicGenre> = emptyList(),
    val searchResults: List<Song> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicDataSource: MusicDataSource
) : ViewModel() {

    private val defaultGenres = listOf(
        MusicGenre("g1", R.string.genre_pop, "Pop", 0xFF2A4D3E),
        MusicGenre("g2", R.string.genre_hiphop, "Hip Hop", 0xFF3D4F32),
        MusicGenre("g3", R.string.genre_rock, "Rock", 0xFF4A3E30),
        MusicGenre("g4", R.string.genre_electronic, "Electronic", 0xFF284852),
        MusicGenre("g5", R.string.genre_acoustic, "Acoustic", 0xFF334A3E),
        MusicGenre("g6", R.string.genre_jazz, "Jazz", 0xFF483A4A),
        MusicGenre("g7", R.string.genre_workout, "Workout", 0xFF424A2D),
        MusicGenre("g8", R.string.genre_focus, "Focus", 0xFF223A36)
    )

    private val _uiState = MutableStateFlow(
        SearchUiState(genres = defaultGenres)
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(newQuery: String) {
        val trimmed = newQuery.trim()
        _uiState.value = _uiState.value.copy(
            query = newQuery,
            isSearching = trimmed.isNotEmpty()
        )

        searchJob?.cancel()

        if (trimmed.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(),
                isLoadingResults = false
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(400) // Debounce typing
            _uiState.value = _uiState.value.copy(isLoadingResults = true)
            try {
                val results = musicDataSource.searchSongs(trimmed)
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isLoadingResults = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    searchResults = emptyList(),
                    isLoadingResults = false,
                    errorMessage = e.message
                )
            }
        }
    }

    fun onClearQuery() {
        searchJob?.cancel()
        _uiState.value = _uiState.value.copy(
            query = "",
            isSearching = false,
            isLoadingResults = false,
            searchResults = emptyList()
        )
    }

    fun onFilterSelected(filter: SearchFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun onGenreClick(genre: MusicGenre) {
        onQueryChanged(genre.searchQuery)
    }
}
