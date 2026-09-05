package com.fitly.di

import com.fitly.domain.generator.OutfitGenerator
import com.fitly.presentation.history.HistoryViewModel
import com.fitly.presentation.home.HomeViewModel
import com.fitly.presentation.wardrobe.WardrobeViewModel
import com.fitly.presentation.wardrobe.additem.AddItemViewModel
import com.fitly.presentation.wardrobe.itemdetail.ItemDetailViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    singleOf(::OutfitGenerator)
    viewModelOf(::AddItemViewModel)
    viewModelOf(::WardrobeViewModel)
    viewModelOf(::ItemDetailViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::HistoryViewModel)
}
