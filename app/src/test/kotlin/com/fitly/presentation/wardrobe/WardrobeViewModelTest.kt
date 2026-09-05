package com.fitly.presentation.wardrobe

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.domain.util.Result
import com.fitly.fakes.FakeClothingItemLocalDataSource
import com.fitly.testutil.MainDispatcherExtension
import com.fitly.testutil.testClothingItem
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class WardrobeViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val clothingItemDataSource = FakeClothingItemLocalDataSource()

    @Test
    fun `state starts empty when the wardrobe has no items`() = runTest {
        val viewModel = WardrobeViewModel(clothingItemDataSource)

        viewModel.state.test {
            assertThat(awaitItem().visibleItems).isEmpty()
        }
    }

    @Test
    fun `state reflects items already saved in the wardrobe`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))

        val viewModel = WardrobeViewModel(clothingItemDataSource)

        viewModel.state.test {
            assertThat(awaitItem().visibleItems.map { it.type }).containsExactly(ClothingType.TOP, ClothingType.SHOES)
        }
    }

    @Test
    fun `selecting a type filter narrows visible items to that type`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        val viewModel = WardrobeViewModel(clothingItemDataSource)

        viewModel.state.test {
            awaitItem() // initial, both items visible

            viewModel.onAction(WardrobeAction.OnTypeFilterSelected(ClothingType.SHOES))

            assertThat(awaitItem().visibleItems.map { it.type }).containsExactly(ClothingType.SHOES)
        }
    }

    @Test
    fun `selecting an occasion filter narrows visible items to that occasion`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP, occasion = Occasion.CASUAL))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES, occasion = Occasion.FORMAL))
        val viewModel = WardrobeViewModel(clothingItemDataSource)

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(WardrobeAction.OnOccasionFilterSelected(Occasion.FORMAL))

            assertThat(awaitItem().visibleItems.map { it.occasion }).containsExactly(Occasion.FORMAL)
        }
    }

    @Test
    fun `selecting a season filter narrows visible items to that season`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP, season = Season.SUMMER))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES, season = Season.WINTER))
        val viewModel = WardrobeViewModel(clothingItemDataSource)

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(WardrobeAction.OnSeasonFilterSelected(Season.WINTER))

            assertThat(awaitItem().visibleItems.map { it.season }).containsExactly(Season.WINTER)
        }
    }

    @Test
    fun `clearing filters shows every item again`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        val viewModel = WardrobeViewModel(clothingItemDataSource)
        viewModel.onAction(WardrobeAction.OnTypeFilterSelected(ClothingType.SHOES))

        viewModel.state.test {
            assertThat(awaitItem().visibleItems.map { it.type }).containsExactly(ClothingType.SHOES)

            viewModel.onAction(WardrobeAction.OnClearFilters)

            assertThat(awaitItem().visibleItems.map { it.type }).containsExactly(ClothingType.TOP, ClothingType.SHOES)
        }
    }

    @Test
    fun `clicking an item emits NavigateToItemDetail with its id`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP)) as Result.Success
        val viewModel = WardrobeViewModel(clothingItemDataSource)

        viewModel.events.test {
            viewModel.onAction(WardrobeAction.OnItemClick(saved.data))
            assertThat(awaitItem()).isEqualTo(WardrobeEvent.NavigateToItemDetail(saved.data))
        }
    }
}
