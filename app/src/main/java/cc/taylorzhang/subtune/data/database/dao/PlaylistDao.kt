package cc.taylorzhang.subtune.data.database.dao

import androidx.room.*
import cc.taylorzhang.subtune.model.Playlist

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    suspend fun getAll(): List<Playlist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlist: Playlist): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(playlists: List<Playlist>): List<Long>

    @Update
    suspend fun update(playlist: Playlist)

    @Transaction
    suspend fun insertOrUpdate(playlist: Playlist) {
        if (insert(playlist) == -1L) {
            update(playlist)
        }
    }

    @Query("DELETE FROM playlist")
    suspend fun clearAll()
}