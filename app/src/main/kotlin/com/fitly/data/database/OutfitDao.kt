package com.fitly.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fitly.domain.model.OutfitStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits WHERE status != :excludedStatus ORDER BY createdAt DESC")
    fun observeHistory(excludedStatus: OutfitStatus = OutfitStatus.PENDING): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE id = :id")
    suspend fun getById(id: Long): OutfitEntity?

    @Upsert
    suspend fun upsert(outfit: OutfitEntity): Long

    @Query("UPDATE outfits SET status = :status WHERE id = :id")
    suspend fun setStatus(id: Long, status: OutfitStatus): Int

    @Query("UPDATE outfits SET favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean): Int
}
