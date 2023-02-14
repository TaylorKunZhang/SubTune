package cc.taylorzhang.subtune.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cc.taylorzhang.subtune.data.database.entity.PagingPage

@Dao
interface PagingPageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: PagingPage)

    @Query("SELECT * FROM paging_page WHERE type = :type")
    suspend fun findByType(type: String): PagingPage?

    @Query("DELETE FROM paging_page WHERE type = :type")
    suspend fun deleteByType(type: String)

    @Query("DELETE FROM paging_page")
    suspend fun clearAll()
}