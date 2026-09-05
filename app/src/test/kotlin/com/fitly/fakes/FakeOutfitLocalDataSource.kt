package com.fitly.fakes

import com.fitly.domain.datasource.OutfitLocalDataSource
import com.fitly.domain.model.Outfit
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeOutfitLocalDataSource : OutfitLocalDataSource {
    private val outfits = MutableStateFlow<List<Outfit>>(emptyList())
    var upsertError: DataError.Local? = null
    var setStatusError: DataError.Local? = null
    var setFavoriteError: DataError.Local? = null

    /** When set, upsert() suspends here until the test completes it - lets a
     * test interleave a second action while the first call is still in flight. */
    var upsertGate: CompletableDeferred<Unit>? = null
    private var nextId = 1L

    val all: List<Outfit> get() = outfits.value

    override fun observeHistory() = outfits.map { list ->
        list.filter { it.status != OutfitStatus.PENDING }.sortedByDescending { it.createdAt }
    }

    override suspend fun getById(id: Long): Result<Outfit, DataError.Local> {
        val outfit = outfits.value.find { it.id == id } ?: return Result.Error(DataError.Local.NOT_FOUND)
        return Result.Success(outfit)
    }

    override suspend fun upsert(outfit: Outfit): Result<Long, DataError.Local> {
        upsertGate?.await()
        upsertError?.let { return Result.Error(it) }
        val id = if (outfit.id != 0L) outfit.id else nextId++
        if (id >= nextId) nextId = id + 1
        val saved = outfit.copy(id = id)
        outfits.update { current -> current.filterNot { it.id == id } + saved }
        return Result.Success(id)
    }

    override suspend fun setStatus(id: Long, status: OutfitStatus): EmptyResult<DataError.Local> {
        setStatusError?.let { return Result.Error(it) }
        val existing = outfits.value.find { it.id == id } ?: return Result.Error(DataError.Local.NOT_FOUND)
        outfits.update { current -> current.filterNot { it.id == id } + existing.copy(status = status) }
        return Result.Success(Unit)
    }

    override suspend fun setFavorite(id: Long, favorite: Boolean): EmptyResult<DataError.Local> {
        setFavoriteError?.let { return Result.Error(it) }
        val existing = outfits.value.find { it.id == id } ?: return Result.Error(DataError.Local.NOT_FOUND)
        outfits.update { current -> current.filterNot { it.id == id } + existing.copy(favorite = favorite) }
        return Result.Success(Unit)
    }
}
