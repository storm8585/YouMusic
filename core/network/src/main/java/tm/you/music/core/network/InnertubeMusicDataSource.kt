package tm.you.music.core.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import tm.you.music.core.common.network.Dispatcher
import tm.you.music.core.common.network.YouMusicDispatchers
import tm.you.music.core.model.AudioStream
import tm.you.music.core.model.Song
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InnertubeMusicDataSource @Inject constructor(
    @Dispatcher(YouMusicDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : MusicDataSource {

    private val searchUrl = "https://music.youtube.com/youtubei/v1/search?prettyPrint=false"
    private val playerUrl = "https://music.youtube.com/youtubei/v1/player?prettyPrint=false"

    override suspend fun searchSongs(query: String): List<Song> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("context", createWebRemixContext())
            put("query", query)
            put("params", "EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D")
        }

        val jsonString = postJson(searchUrl, payload.toString()) ?: return@withContext emptyList()
        parseSearchResponse(jsonString)
    }

    override suspend fun getTrendingSongs(): List<Song> = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("context", createWebRemixContext())
            put("query", "Top Music Hits")
            put("params", "EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D")
        }

        val jsonString = postJson(searchUrl, payload.toString()) ?: return@withContext emptyList()
        parseSearchResponse(jsonString)
    }

    override suspend fun getStreamUrl(songId: String): AudioStream = withContext(ioDispatcher) {
        val payload = JSONObject().apply {
            put("context", createAndroidVrContext())
            put("videoId", songId)
        }

        val jsonString = postJson(playerUrl, payload.toString())
        if (jsonString != null) {
            val directStream = parsePlayerResponse(jsonString)
            if (directStream != null) return@withContext directStream
        }

        fetchPipedFallbackStream(songId)
    }

    private fun createWebRemixContext(): JSONObject {
        val client = JSONObject().apply {
            put("clientName", "WEB_REMIX")
            put("clientVersion", "1.20240101.01.00")
            put("hl", "tr")
            put("gl", "TR")
        }
        return JSONObject().apply {
            put("client", client)
        }
    }

    private fun createAndroidVrContext(): JSONObject {
        val client = JSONObject().apply {
            put("clientName", "ANDROID_VR")
            put("clientVersion", "1.65.10")
            put("osName", "Android")
            put("osVersion", "12")
            put("deviceModel", "Quest 3")
            put("hl", "en")
            put("gl", "US")
        }
        return JSONObject().apply {
            put("client", client)
        }
    }

    private fun postJson(urlString: String, jsonBody: String): String? {
        return try {
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                setRequestProperty("X-Goog-Api-Key", "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8")
                setRequestProperty("X-Origin", "https://music.youtube.com")
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonBody)
                writer.flush()
            }

            if (connection.responseCode in 200..299) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    reader.readText()
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseSearchResponse(jsonString: String): List<Song> {
        val songs = mutableListOf<Song>()
        try {
            val root = JSONObject(jsonString)
            val contents = root.optJSONObject("contents") ?: return emptyList()
            val tabbedSearchResults = contents.optJSONObject("tabbedSearchResultsRenderer") ?: return emptyList()
            val tabs = tabbedSearchResults.optJSONArray("tabs") ?: return emptyList()
            if (tabs.length() == 0) return emptyList()

            val tabRenderer = tabs.getJSONObject(0).optJSONObject("tabRenderer") ?: return emptyList()
            val content = tabRenderer.optJSONObject("content") ?: return emptyList()
            val sectionListRenderer = content.optJSONObject("sectionListRenderer") ?: return emptyList()
            val sectionContents = sectionListRenderer.optJSONArray("contents") ?: return emptyList()

            for (i in 0 until sectionContents.length()) {
                val section = sectionContents.getJSONObject(i)
                val musicShelf = section.optJSONObject("musicShelfRenderer") ?: continue
                val items = musicShelf.optJSONArray("contents") ?: continue

                for (j in 0 until items.length()) {
                    val item = items.getJSONObject(j)
                    val renderer = item.optJSONObject("musicResponsiveListItemRenderer") ?: continue

                    val videoId = renderer.optJSONObject("playlistItemData")?.optString("videoId")
                        ?: extractVideoIdFromRenderer(renderer)
                        ?: continue

                    val flexColumns = renderer.optJSONArray("flexColumns") ?: continue
                    if (flexColumns.length() == 0) continue

                    val title = extractTextFromFlexColumn(flexColumns.optJSONObject(0)) ?: continue
                    val artist = if (flexColumns.length() > 1) {
                        extractTextFromFlexColumn(flexColumns.optJSONObject(1)) ?: "Unknown Artist"
                    } else {
                        "Unknown Artist"
                    }

                    val artworkUrl = extractThumbnail(renderer)
                    val cleanTitle = cleanMusicTitle(title)

                    songs.add(
                        Song(
                            id = videoId,
                            title = cleanTitle,
                            artist = artist,
                            durationMs = 0L,
                            artworkUrl = artworkUrl
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return songs
    }

    private fun extractVideoIdFromRenderer(renderer: JSONObject): String? {
        val navEndpoint = renderer.optJSONObject("navigationEndpoint")
        val watchEndpoint = navEndpoint?.optJSONObject("watchEndpoint")
        return watchEndpoint?.optString("videoId")
    }

    private fun extractTextFromFlexColumn(column: JSONObject?): String? {
        if (column == null) return null
        val flexColumnRenderer = column.optJSONObject("musicResponsiveListItemFlexColumnRenderer") ?: return null
        val text = flexColumnRenderer.optJSONObject("text") ?: return null
        val runs = text.optJSONArray("runs") ?: return null
        if (runs.length() == 0) return null
        return runs.getJSONObject(0).optString("text")
    }

    private fun extractThumbnail(renderer: JSONObject): String? {
        val thumbnailRenderer = renderer.optJSONObject("thumbnail")?.optJSONObject("musicThumbnailRenderer") ?: return null
        val thumbnailObj = thumbnailRenderer.optJSONObject("thumbnail") ?: return null
        val thumbnails = thumbnailObj.optJSONArray("thumbnails") ?: return null
        if (thumbnails.length() == 0) return null
        val last = thumbnails.getJSONObject(thumbnails.length() - 1)
        val rawUrl = last.optString("url")
        return if (rawUrl.startsWith("//")) "https:$rawUrl" else rawUrl
    }

    private fun cleanMusicTitle(title: String): String {
        return title.replace(Regex("(?i)\\[official.*?\\]|\\(official.*?\\)|\\(video\\)|\\[video\\]|\\(lyrics?\\)|\\[lyrics?\\]|\\(audio\\)|\\[audio\\]|\\(klip\\)|\\[klip\\]"), "")
            .trim()
    }

    private fun parsePlayerResponse(jsonString: String): AudioStream? {
        return try {
            val root = JSONObject(jsonString)
            val streamingData = root.optJSONObject("streamingData") ?: return null
            val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats") ?: return null

            var selectedUrl: String? = null
            var highestBitrate = 0
            var mimeType = "audio/mp4"

            for (i in 0 until adaptiveFormats.length()) {
                val format = adaptiveFormats.getJSONObject(i)
                val formatMime = format.optString("mimeType")
                if (formatMime.startsWith("audio/")) {
                    val url = format.optString("url")
                    val bitrate = format.optInt("bitrate", 0)
                    if (url.isNotEmpty() && bitrate >= highestBitrate) {
                        highestBitrate = bitrate
                        selectedUrl = url
                        mimeType = formatMime
                    }
                }
            }

            if (selectedUrl != null) {
                AudioStream(
                    url = selectedUrl,
                    bitrate = highestBitrate,
                    format = mimeType
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchPipedFallbackStream(songId: String): AudioStream {
        val fallbackUrl = "https://pipedapi.kavin.rocks/streams/$songId"
        try {
            val url = URL(fallbackUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }

            if (connection.responseCode in 200..299) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                val root = JSONObject(response)
                val audioStreams = root.optJSONArray("audioStreams")
                if (audioStreams != null && audioStreams.length() > 0) {
                    val bestStream = audioStreams.getJSONObject(0)
                    val streamUrl = bestStream.optString("url")
                    val bitrate = bestStream.optInt("bitrate", 128000)
                    val format = bestStream.optString("format", "m4a")
                    return AudioStream(url = streamUrl, bitrate = bitrate, format = format)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return AudioStream(url = "", bitrate = 0, format = "")
    }
}
