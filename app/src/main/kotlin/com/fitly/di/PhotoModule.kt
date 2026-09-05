package com.fitly.di

import com.fitly.data.photo.FilePhotoLocalDataSource
import com.fitly.data.photo.PaletteDominantColorExtractor
import com.fitly.domain.datasource.DominantColorExtractor
import com.fitly.domain.datasource.PhotoLocalDataSource
import org.koin.dsl.bind
import org.koin.dsl.module

val photoModule = module {
    single { PaletteDominantColorExtractor() } bind DominantColorExtractor::class
    single { FilePhotoLocalDataSource(get()) } bind PhotoLocalDataSource::class
}
