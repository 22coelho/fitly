package com.fitly.di

import com.fitly.presentation.wardrobe.WardrobeViewModel
import com.fitly.presentation.wardrobe.additem.AddItemViewModel
import com.fitly.presentation.wardrobe.itemdetail.ItemDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::AddItemViewModel)
    viewModelOf(::WardrobeViewModel)
    viewModelOf(::ItemDetailViewModel)
}
