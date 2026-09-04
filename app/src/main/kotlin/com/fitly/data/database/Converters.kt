package com.fitly.data.database

import androidx.room.TypeConverter
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.Season

class Converters {
    @TypeConverter
    fun fromClothingType(value: ClothingType): String = value.name

    @TypeConverter
    fun toClothingType(value: String): ClothingType = ClothingType.valueOf(value)

    @TypeConverter
    fun fromOccasion(value: Occasion?): String? = value?.name

    @TypeConverter
    fun toOccasion(value: String?): Occasion? = value?.let(Occasion::valueOf)

    @TypeConverter
    fun fromSeason(value: Season): String = value.name

    @TypeConverter
    fun toSeason(value: String): Season = Season.valueOf(value)

    @TypeConverter
    fun fromCondition(value: Condition): String = value.name

    @TypeConverter
    fun toCondition(value: String): Condition = Condition.valueOf(value)

    @TypeConverter
    fun fromOutfitStatus(value: OutfitStatus): String = value.name

    @TypeConverter
    fun toOutfitStatus(value: String): OutfitStatus = OutfitStatus.valueOf(value)
}
