package tm.you.music.core.network

import tm.you.music.core.model.AudioStream
import tm.you.music.core.model.Song

interface MusicDataSource {
    suspend fun searchSongs(query: String): List<Song>
    suspend fun getStreamUrl(songId: String): AudioStream
    suspend fun getTrendingSongs(): List<Song>
}
