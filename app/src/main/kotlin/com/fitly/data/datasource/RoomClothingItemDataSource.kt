package com.fitly.data.datasource

import com.fitly.data.database.ClothingItemDao
import com.fitly.data.util.safeRoomCall
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomClothingItemDataSource(
    private val dao: ClothingItemDao,
) : ClothingItemLocalDataSource {

    override fun observeAll(): Flow<List<ClothingItem>> =
        dao.observeAll().map { entities -> entities.map { it.toClothingItem() } }

    override suspend fun getById(id: Long): Result<ClothingItem, DataError.Local> {
        val entity = dao.getById(id) ?: return Result.Error(DataError.Local.NOT_FOUND)
        return Result.Success(entity.toClothingItem())
    }

    override suspend fun upsert(item: ClothingItem): Result<Long, DataError.Local> = safeRoomCall {
        val generatedId = dao.upsert(item.toEntity())
        // Room's @Upsert returns -1 on the update branch; the entity's own id is
        // already the real one whenever this is an update (id != 0).
        if (item.id != 0L) item.id else generatedId
    }

    override suspend fun delete(id: Long): EmptyResult<DataError.Local> {
        val result = safeRoomCall { dao.deleteById(id) }
        return when (result) {
            is Result.Error -> result
            is Result.Success -> if (result.data == 0) {
                Result.Error(DataError.Local.NOT_FOUND)
            } else {
                Result.Success(Unit)
            }
        }
    }
}
