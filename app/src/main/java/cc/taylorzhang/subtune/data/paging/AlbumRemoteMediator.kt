package cc.taylorzhang.subtune.data.paging

import androidx.paging.*
import androidx.room.withTransaction
import cc.taylorzhang.subtune.data.database.AppDatabase
import cc.taylorzhang.subtune.data.database.entity.PagingPage
import cc.taylorzhang.subtune.data.repository.MusicRepository
import cc.taylorzhang.subtune.model.*
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalPagingApi::class)
class AlbumRemoteMediator(
    private val type: String,
    private val musicRepository: MusicRepository,
    private val db: AppDatabase,
) : RemoteMediator<Int, Album>(), KoinComponent {

    private val albumDao = db.albumDao()
    private val pagingPageDao = db.pagingPageDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Album>): MediatorResult {
        val currentPage = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val pagingPage = db.withTransaction {
                    pagingPageDao.findByType(PagingType.ALBUM)
                } ?: return MediatorResult.Success(endOfPaginationReached = true)
                pagingPage.nextPage
            }
        } ?: 0
        val pageSize = state.config.pageSize

        val result = musicRepository.getAlbumList2(type, pageSize, currentPage * pageSize)
        return when (result) {
            is ApiSuccess -> {
                db.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        pagingPageDao.deleteByType(PagingType.ALBUM)
                        albumDao.clearAll()
                    }
                    pagingPageDao.insert(PagingPage(PagingType.ALBUM, currentPage + 1))
                    albumDao.insertAll(result.data)
                }
                MediatorResult.Success(endOfPaginationReached = result.data.isEmpty())
            }
            is ApiError -> {
                MediatorResult.Error(Exception(result.error.message))
            }
        }
    }
}