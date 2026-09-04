package com.fitly.di

import androidx.room.Room
import com.fitly.data.database.FitlyDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), FitlyDatabase::class.java, "fitly.db").build()
    }
    single { get<FitlyDatabase>().clothingItemDao() }
    single { get<FitlyDatabase>().outfitDao() }
}
