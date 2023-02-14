package cc.taylorzhang.subtune.data.database.dao

import androidx.room.*
import cc.taylorzhang.subtune.data.database.entity.PlaylistWithSong
import cc.taylorzhang.subtune.data.database.model.PlaylistSongPair

@Dao
interface PlaylistWithSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(join: PlaylistWithSong)

    @Transaction
    @Query("SELECT * FROM playlist WHERE id = :id")
    suspend fun findById(id: String): PlaylistSongPair?

    @Transaction
    @Query("DELETE FROM playlist_song WHERE playlist_id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM playlist_song")
    suspend fun clearAll()
}