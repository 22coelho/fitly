package com.fitly.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus

@Entity(
    tableName = "outfits",
    foreignKeys = [
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["topItemId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["bottomItemId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["shoesItemId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ClothingItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["accessoryItemId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("topItemId"),
        Index("bottomItemId"),
        Index("shoesItemId"),
        Index("accessoryItemId"),
        Index("status"),
        Index("createdAt"),
    ],
)
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topItemId: Long,
    val bottomItemId: Long,
    val shoesItemId: Long,
    val accessoryItemId: Long?,
    val occasion: Occasion?,
    val status: OutfitStatus,
    val favorite: Boolean,
    val createdAt: Long,
)
