package com.fitly.data.datasource

import com.fitly.data.database.OutfitDao
import com.fitly.data.util.safeLocalCall
import com.fitly.data.util.toEmptyResultOrNotFound
import com.fitly.domain.datasource.OutfitLocalDataSource
import com.fitly.domain.model.Outfit
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomOutfitDataSource(
    private val dao: OutfitDao,
) : OutfitLocalDataSource {

    override fun observeHistory(): Flow<List<Outfit>> =
        dao.observeHistory().map { entities -> entities.map { it.toOutfit() } }

    override suspend fun getById(id: Long): Result<Outfit, DataError.Local> {
        val entity = dao.getById(id) ?: return Result.Error(DataError.Local.NOT_FOUND)
        return Result.Success(entity.toOutfit())
    }

    override suspend fun upsert(outfit: Outfit): Result<Long, DataError.Local> = safeLocalCall {
        val generatedId = dao.upsert(outfit.toEntity())
        // Room's @Upsert returns -1 on the update branch; the entity's own id is
        // already the real one whenever this is an update (id != 0).
        if (outfit.id != 0L) outfit.id else generatedId
    }

    override suspend fun setStatus(id: Long, status: OutfitStatus): EmptyResult<DataError.Local> {
        val result = safeLocalCall { dao.setStatus(id, status) }
        return result.toEmptyResultOrNotFound()
    }

    override suspend fun setFavorite(id: Long, favorite: Boolean): EmptyResult<DataError.Local> {
        val result = safeLocalCall { dao.setFavorite(id, favorite) }
        return result.toEmptyResultOrNotFound()
    }
}
