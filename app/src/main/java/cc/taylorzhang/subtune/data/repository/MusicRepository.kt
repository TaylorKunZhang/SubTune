package cc.taylorzhang.subtune.data.repository

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cc.taylorzhang.subtune.data.database.AppDatabase
import cc.taylorzhang.subtune.data.database.entity.PlaylistWithSong
import cc.taylorzhang.subtune.data.http.HttpUtil
import cc.taylorzhang.subtune.data.http.SubsonicApi
import cc.taylorzhang.subtune.data.paging.AlbumRemoteMediator
import cc.taylorzhang.subtune.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class MusicRepository(
    private val context: Context,
    private val subsonicApi: SubsonicApi,
    private val db: AppDatabase,
) {

    companion object {
        private const val MUSIC_CACHE_DIR_NAME = "music_cache"
        private const val LYRICS_CACHE_DIR_NAME = "lyrics_cache"
        private const val cacheSize = 4 * 1024 * 1024 * 1024L // TODO: Custom Cache Size
    }

    private val musicCacheDir by lazy { context.cacheDir.resolve(MUSIC_CACHE_DIR_NAME) }
    private val lyricsCacheDir by lazy { context.cacheDir.resolve(LYRICS_CACHE_DIR_NAME) }
    val cacheDataSourceFactory by lazy { initCacheDataSourceFactory() }

    /**
     * Returns details for an album, including a list of songs.
     * This method organizes music according to ID3 tags.
     */
    suspend fun getAlbum(id: String): ApiResult<Album> {
        return HttpUtil.apiCall(
            call = { subsonicApi.getAlbum(id).response },
            saveCacheBlock = {
                db.albumDao().insertOrUpdate(it)
                db.songDao().insertAll(it.song)
            },
            readCacheBlock = {
                db.albumDao().findByIdWithSongs(id)?.apply {
                    album.song = songs
                }?.album
            }
        )
    }

    /**
     * Returns a list of albums.
     * This method organizes music according to ID3 tags.
     */
    suspend fun getAlbumList2(type: String, size: Int?, offset: Int): ApiResult<List<Album>> {
        return HttpUtil.apiCall { subsonicApi.getAlbumList2(type, size, offset).response }
    }

    /**
     * Returns flow containing a list of albums.
     */
    @kotlin.OptIn(ExperimentalPagingApi::class)
    fun fetchAlbumPagingData(type: String): Flow<PagingData<Album>> {
        return Pager(
            config = PagingConfig(
                pageSize = 100,
                prefetchDistance = 10,
                initialLoadSize = 100
            ),
            remoteMediator = AlbumRemoteMediator(type, this, db),
            pagingSourceFactory = { db.albumDao().getAll() }
        ).flow
    }

    /**
     * Returns all playlists a user is allowed to play.
     */
    suspend fun getPlaylists(): ApiResult<List<Playlist>> {
        return HttpUtil.apiCall(
            call = { subsonicApi.getPlaylists().response },
            saveCacheBlock = {
                db.playlistDao().clearAll()
                db.playlistDao().insertAll(it)
            },
            readCacheBlock = {
                db.playlistDao().getAll()
            }
        )
    }

    /**
     * Returns a listing of files in a saved playlist.
     */
    suspend fun getPlaylist(id: String): ApiResult<Playlist> {
        return HttpUtil.apiCall(
            call = { subsonicApi.getPlaylist(id).response },
            saveCacheBlock = {
                db.playlistDao().insertOrUpdate(it)
                db.songDao().insertAll(it.entry)
                db.playlistWithSongDao().deleteById(it.id)
                it.entry.forEach { song ->
                    val join = PlaylistWithSong(playlistId = it.id, songId = song.id)
                    db.playlistWithSongDao().insert(join)
                }
            },
            readCacheBlock = {
                db.playlistWithSongDao().findById(id)?.apply {
                    playlist.entry = songs
                }?.playlist
            }
        )
    }

    /**
     * Returns albums, artists and songs matching the given search criteria.
     */
    suspend fun search3(query: String): ApiResult<Search3Result> {
        return HttpUtil.apiCall { subsonicApi.search3(query).response }
    }

    /**
     * Searches for and returns lyrics for a given song.
     */
    suspend fun getLyrics(id: String, artist: String, title: String): ApiResult<Lyrics> {
        return HttpUtil.apiCall(
            call = { subsonicApi.getLyrics(artist, title).response },
            saveCacheBlock = {
                withContext(Dispatchers.IO) {
                    if (!lyricsCacheDir.exists()) {
                        lyricsCacheDir.mkdir()
                    }
                    File(lyricsCacheDir, id).writeText(it.value)
                }
            },
            readCacheBlock = {
                val file = File(lyricsCacheDir, id)
                if (file.exists()) {
                    withContext(Dispatchers.IO) {
                        Lyrics(artist = artist, title = title, value = file.readText())
                    }
                } else {
                    null
                }
            },
        )
    }

    /**
     * Returns random songs matching the given criteria.
     */
    suspend fun getRandomSongs(size: Int): ApiResult<List<Song>> {
        return HttpUtil.apiCall(
            call = { subsonicApi.getRandomSongs(size).response },
            saveCacheBlock = { db.songDao().insertAll(it) },
            readCacheBlock = {
                val list = db.songDao().getAll()
                if (list.size <= size) {
                    list.shuffled()
                } else {
                    list.shuffled().take(size)
                }
            }
        )
    }

    /**
     * Returns a cover art image uri.
     *
     * @param id The ID of a song, album or artist.
     */
    fun getCoverArtUri(id: String): Uri {
        return Uri.parse(HttpUtil.baseUrl()).buildUpon()
            .appendEncodedPath("rest/getCoverArt")
            .appendQueryParameter("id", id)
            .apply { HttpUtil.addAuthParameter(this) }
            .build()
    }

    /**
     * Returns a given media file stream uri.
     *
     * @param id A string which uniquely identifies the file to stream.
     * @param maxBitRate Server will attempt to limit the bitrate to this value, in kilobits per second.
     * If set to zero, no limit is imposed.
     */
    fun getSongStreamUri(id: String, maxBitRate: Int): Uri {
        return Uri.parse(HttpUtil.baseUrl()).buildUpon()
            .appendEncodedPath("rest/stream")
            .appendQueryParameter("id", id)
            .appendQueryParameter("maxBitRate", maxBitRate.toString())
            .apply { HttpUtil.addAuthParameter(this) }
            .build()
    }

    @OptIn(UnstableApi::class)
    suspend fun clearCache() = withContext(Dispatchers.IO) {
        db.clearAllTables()
        deleteFile(musicCacheDir)
        deleteFile(lyricsCacheDir)
    }

    suspend fun clearAlbumCache() {
        db.albumDao().clearAll()
    }

    @OptIn(UnstableApi::class)
    private fun initCacheDataSourceFactory(): CacheDataSource.Factory {
        val databaseProvider = StandaloneDatabaseProvider(context)
        val cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize)
        val simpleCache = SimpleCache(musicCacheDir, cacheEvictor, databaseProvider)

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent(HttpUtil.APPLICATION_IDENTITY)
        return CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
    }

    private fun deleteFile(file: File) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { deleteFile(it) }
            file.delete()
        } else if (file.exists()) {
            file.delete()
        }
    }
}