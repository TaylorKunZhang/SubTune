package cc.taylorzhang.subtune.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cc.taylorzhang.subtune.model.Song

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    suspend fun getAll(): List<Song>

    @Query("SELECT * FROM song WHERE id = :id ")
    suspend fun findById(id: String): Song

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(songs: List<Song>)

    @Query("DELETE FROM song")
    suspend fun clearAll()
}