package cc.taylorzhang.subtune.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paging_page")
class PagingPage(
    @PrimaryKey
    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "next_page")
    val nextPage: Int,
)