package com.fitly.di

import com.fitly.presentation.wardrobe.additem.AddItemViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::AddItemViewModel)
}
