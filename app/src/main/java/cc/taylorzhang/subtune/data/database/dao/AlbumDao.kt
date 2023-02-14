package cc.taylorzhang.subtune.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import cc.taylorzhang.subtune.data.database.model.AlbumSongPair
import cc.taylorzhang.subtune.model.Album

@Dao
interface AlbumDao {
    @Query("SELECT * FROM album")
    fun getAll(): PagingSource<Int, Album>

    @Transaction
    @Query("SELECT * FROM album WHERE id = :id")
    suspend fun findByIdWithSongs(id: String): AlbumSongPair?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: Album): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(albums: List<Album>): List<Long>

    @Update
    suspend fun update(album: Album)

    @Transaction
    suspend fun insertOrUpdate(album: Album) {
        if (insert(album) == -1L) {
            update(album)
        }
    }

    @Query("DELETE FROM album")
    suspend fun clearAll()
}