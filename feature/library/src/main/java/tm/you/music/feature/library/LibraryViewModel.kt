package tm.you.music.feature.library

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tm.you.music.core.database.dao.SongDao
import tm.you.music.core.database.model.asExternalModel
import tm.you.music.core.designsystem.R
import tm.you.music.core.model.Song
import javax.inject.Inject

data class Playlist(
    val id: String,
    val title: String,
    val creator: String,
    val trackCount: Int,
    val artworkUrl: String? = null
)

enum class LibraryFilter(@StringRes val labelRes: Int) {
    ALL(R.string.category_all),
    PLAYLISTS(R.string.library_playlists),
    LIKED_SONGS(R.string.library_liked_songs)
}

data class LibraryUiState(
    val filters: List<LibraryFilter> = LibraryFilter.entries,
    val selectedFilter: LibraryFilter = LibraryFilter.ALL,
    val favoriteSongs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val songDao: SongDao
) : ViewModel() {

    private val selectedFilterFlow = MutableStateFlow(LibraryFilter.ALL)

    val uiState: StateFlow<LibraryUiState> = combine(
        selectedFilterFlow,
        songDao.getFavoriteSongs().map { list -> list.map { it.asExternalModel() } }
    ) { filter, favorites ->
        LibraryUiState(
            selectedFilter = filter,
            favoriteSongs = favorites,
            playlists = emptyList(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LibraryUiState()
    )

    fun onFilterSelected(filter: LibraryFilter) {
        selectedFilterFlow.value = filter
    }
}
