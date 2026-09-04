package com.fitly.di

import com.fitly.data.datasource.RoomClothingItemDataSource
import com.fitly.data.datasource.RoomOutfitDataSource
import com.fitly.domain.datasource.ClothingItemLocalDataSource
import com.fitly.domain.datasource.OutfitLocalDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::RoomClothingItemDataSource) { bind<ClothingItemLocalDataSource>() }
    singleOf(::RoomOutfitDataSource) { bind<OutfitLocalDataSource>() }
}
