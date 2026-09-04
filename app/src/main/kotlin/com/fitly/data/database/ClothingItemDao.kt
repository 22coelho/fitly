package com.fitly.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingItemDao {
    @Query("SELECT * FROM clothing_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE id = :id")
    suspend fun getById(id: Long): ClothingItemEntity?

    @Upsert
    suspend fun upsert(item: ClothingItemEntity): Long

    @Query("DELETE FROM clothing_items WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
