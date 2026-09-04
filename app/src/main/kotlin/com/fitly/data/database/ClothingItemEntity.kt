package com.fitly.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season

@Entity(tableName = "clothing_items", indices = [Index("createdAt")])
data class ClothingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoPath: String,
    val dominantColor: Int,
    val type: ClothingType,
    val occasion: Occasion,
    val season: Season,
    val condition: Condition,
    val createdAt: Long,
)
