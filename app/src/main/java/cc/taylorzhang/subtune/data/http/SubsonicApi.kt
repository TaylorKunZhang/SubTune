package cc.taylorzhang.subtune.data.http

import cc.taylorzhang.subtune.data.http.response.*
import retrofit2.http.GET
import retrofit2.http.Query

interface SubsonicApi {

    /**
     * Used to test connectivity with the server.
     */
    @GET("/rest/ping")
    suspend fun ping(): PingResponse

    /**
     * Returns details for an album, including a list of songs.
     * This method organizes music according to ID3 tags.
     *
     * @param id The album ID.
     */
    @GET("/rest/getAlbum")
    suspend fun getAlbum(@Query("id") id: String): GetAlbumResponse

    /**
     * Returns a list of albums.
     * This method organizes music according to ID3 tags.
     *
     * @param type The list type. Must be one of the following: random, newest, frequent, recent,
     *     starred, alphabeticalByName or alphabeticalByArtist.
     * @param size The number of albums to return. Max 500. Default 10.
     * @param offset The list offset. Useful if you for example want to page through the list
     *     of newest albums. Default 0.
     */
    @GET("/rest/getAlbumList2")
    suspend fun getAlbumList2(
        @Query("type") type: String,
        @Query("size") size: Int?,
        @Query("offset") offset: Int,
    ): GetAlbumList2Response

    /**
     * Returns all playlists a user is allowed to play.
     */
    @GET("/rest/getPlaylists")
    suspend fun getPlaylists(): GetPlaylistsResponse

    /**
     * Returns a listing of files in a saved playlist.
     *
     * @param id The playlist ID.
     */
    @GET("/rest/getPlaylist")
    suspend fun getPlaylist(@Query("id") id: String): GetPlaylistResponse

    /**
     * Returns albums, artists and songs matching the given search criteria.
     * This method organizes music according to ID3 tags.
     *
     * @param query Search query.
     */
    @GET("/rest/search3")
    suspend fun search3(@Query("query") query: String): Search3Response

    /**
     * Searches for and returns lyrics for a given song.
     */
    @GET("/rest/getLyrics")
    suspend fun getLyrics(
        @Query("artist") artist: String,
        @Query("title") title: String
    ): GetLyricsResponse
}