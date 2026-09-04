package com.fitly.domain.datasource

import com.fitly.domain.model.ClothingItem
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface ClothingItemLocalDataSource {
    fun observeAll(): Flow<List<ClothingItem>>
    suspend fun getById(id: Long): Result<ClothingItem, DataError.Local>
    suspend fun upsert(item: ClothingItem): Result<Long, DataError.Local>
    suspend fun delete(id: Long): EmptyResult<DataError.Local>
}
