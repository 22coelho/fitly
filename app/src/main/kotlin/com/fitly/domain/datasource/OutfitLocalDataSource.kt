package com.fitly.domain.datasource

import com.fitly.domain.model.Outfit
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface OutfitLocalDataSource {
    /** Outfits with status != PENDING, most recent first. */
    fun observeHistory(): Flow<List<Outfit>>
    suspend fun getById(id: Long): Result<Outfit, DataError.Local>
    suspend fun upsert(outfit: Outfit): Result<Long, DataError.Local>
    suspend fun setStatus(id: Long, status: OutfitStatus): EmptyResult<DataError.Local>
    suspend fun setFavorite(id: Long, favorite: Boolean): EmptyResult<DataError.Local>
}
