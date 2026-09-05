package com.fitly.presentation

import androidx.annotation.StringRes
import com.fitly.R
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.model.Season
import com.fitly.domain.util.DataError

/*
 * The one place a domain enum becomes words the user reads. Kept out of `domain/`, which must stay
 * free of android.* and androidx.*, and out of the enums themselves, which have no business
 * knowing a UI exists.
 */

@get:StringRes
val ClothingType.labelRes: Int
    get() = when (this) {
        ClothingType.TOP -> R.string.type_top
        ClothingType.BOTTOM -> R.string.type_bottom
        ClothingType.SHOES -> R.string.type_shoes
        ClothingType.DRESS -> R.string.type_dress
        ClothingType.ACCESSORY -> R.string.type_accessory
    }

@get:StringRes
val Occasion.labelRes: Int
    get() = when (this) {
        Occasion.CASUAL -> R.string.occasion_casual
        Occasion.WORK -> R.string.occasion_work
        Occasion.SPORT -> R.string.occasion_sport
        Occasion.FORMAL -> R.string.occasion_formal
        Occasion.DATE -> R.string.occasion_date
    }

@get:StringRes
val Season.labelRes: Int
    get() = when (this) {
        Season.SUMMER -> R.string.season_summer
        Season.WINTER -> R.string.season_winter
        Season.ALL_YEAR -> R.string.season_all_year
    }

@get:StringRes
val Condition.labelRes: Int
    get() = when (this) {
        Condition.NEW -> R.string.condition_new
        Condition.GOOD -> R.string.condition_good
        Condition.WORN -> R.string.condition_worn
    }

@get:StringRes
val OutfitStatus.labelRes: Int
    get() = when (this) {
        OutfitStatus.ACCEPTED -> R.string.status_accepted
        OutfitStatus.REJECTED -> R.string.status_rejected
        OutfitStatus.PENDING -> R.string.status_pending
    }

@get:StringRes
val DataError.Local.messageRes: Int
    get() = when (this) {
        DataError.Local.DISK_FULL -> R.string.error_disk_full
        DataError.Local.NOT_FOUND -> R.string.error_not_found
        DataError.Local.UNKNOWN -> R.string.error_unknown
    }
