package com.fitly.fakes

import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeClothingItemLocalDataSource : ClothingItemLocalDataSource {
    private val items = MutableStateFlow<List<ClothingItem>>(emptyList())
    var upsertError: DataError.Local? = null

    /** When set, upsert() suspends here until the test completes it - lets a test
     * interleave a second action while the first upsert is still in flight. */
    var upsertGate: CompletableDeferred<Unit>? = null

    private var nextId = 1L

    override fun observeAll() = items

    override suspend fun getById(id: Long): Result<ClothingItem, DataError.Local> {
        val item = items.value.find { it.id == id } ?: return Result.Error(DataError.Local.NOT_FOUND)
        return Result.Success(item)
    }

    override suspend fun upsert(item: ClothingItem): Result<Long, DataError.Local> {
        upsertGate?.await()
        upsertError?.let { return Result.Error(it) }
        val id = if (item.id != 0L) item.id else nextId++
        if (id >= nextId) nextId = id + 1
        val saved = item.copy(id = id)
        items.update { current -> current.filterNot { it.id == id } + saved }
        return Result.Success(id)
    }

    override suspend fun delete(id: Long): EmptyResult<DataError.Local> {
        items.update { current -> current.filterNot { it.id == id } }
        return Result.Success(Unit)
    }
}
