package cc.taylorzhang.subtune.ui.component

import android.os.Parcelable
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.paging.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface LazyPagingItemsProxy<T> {
    val itemSnapshotList: ItemSnapshotList<T>

    val itemCount: Int

    val loadState: CombinedLoadStates

    operator fun get(index: Int): T?

    fun peek(index: Int): T? {
        return itemSnapshotList[index]
    }

    fun retry()

    fun refresh()
}

@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItemsProxy(
    context: CoroutineContext = EmptyCoroutineContext
): LazyPagingItemsProxy<T> {
    val items = collectAsLazyPagingItems(context)
    return LazyPagingItemsProxyImpl(items)
}

fun <T : Any> LazyListScope.items(
    items: LazyPagingItemsProxy<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(item)
            }
        }
    ) { index ->
        itemContent(items[index])
    }
}

fun <T : Any> LazyGridScope.items(
    items: LazyPagingItemsProxy<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(item)
            }
        }
    ) { index ->
        itemContent(items[index])
    }
}

fun <T : Any> LazyListScope.itemsIndexed(
    items: LazyPagingItemsProxy<T>,
    key: ((index: Int, item: T) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(index, item)
            }
        }
    ) { index ->
        itemContent(index, items[index])
    }
}

fun <T : Any> LazyGridScope.itemsIndexed(
    items: LazyPagingItemsProxy<T>,
    key: ((index: Int, item: T) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(index: Int, value: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(index, item)
            }
        }
    ) { index ->
        itemContent(index, items[index])
    }
}

fun <T : Any> lazyPagingItemsPreview(
    items: List<T>,
    placeholdersBefore: Int = 0,
    placeholdersAfter: Int = 0,
    itemCount: Int = items.size,
    refresh: LoadState = LoadState.NotLoading(false),
    prepend: LoadState = LoadState.NotLoading(false),
    append: LoadState = LoadState.NotLoading(false),
    mediator: LoadStates? = null,
): LazyPagingItemsProxy<T> {
    return LazyPagingItemsProxyPreview(
        items = items,
        itemSnapshotList = ItemSnapshotList(
            placeholdersBefore = placeholdersBefore,
            placeholdersAfter = placeholdersAfter,
            items = items,
        ),
        itemCount = itemCount,
        loadState = CombinedLoadStates(
            refresh = refresh,
            prepend = prepend,
            append = append,
            source = LoadStates(
                refresh = refresh,
                prepend = prepend,
                append = append,
            ),
            mediator = mediator,
        ),
    )
}

class LazyPagingItemsProxyImpl<T : Any>(
    private val items: LazyPagingItems<T>,
) : LazyPagingItemsProxy<T> {

    override val itemSnapshotList: ItemSnapshotList<T>
        get() = items.itemSnapshotList

    override val itemCount: Int
        get() = items.itemCount

    override val loadState: CombinedLoadStates
        get() = items.loadState

    override fun get(index: Int): T? {
        return items[index]
    }

    override fun retry() {
        items.retry()
    }

    override fun refresh() {
        items.refresh()
    }
}

class LazyPagingItemsProxyPreview<T : Any>(
    private val items: List<T>,
    override val itemSnapshotList: ItemSnapshotList<T>,
    override val itemCount: Int,
    override val loadState: CombinedLoadStates,
) : LazyPagingItemsProxy<T> {

    override fun get(index: Int): T? {
        return items.getOrNull(index)
    }

    override fun retry() {}

    override fun refresh() {}
}

@Parcelize
private data class PagingPlaceholderKey(private val index: Int) : Parcelable