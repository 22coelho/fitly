package com.fitly.presentation.history

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Outfit
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.util.DataError
import com.fitly.fakes.FakeClothingItemLocalDataSource
import com.fitly.fakes.FakeOutfitLocalDataSource
import com.fitly.testutil.MainDispatcherExtension
import com.fitly.testutil.testClothingItem
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class HistoryViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val clothingItemDataSource = FakeClothingItemLocalDataSource()
    private val outfitDataSource = FakeOutfitLocalDataSource()

    private fun buildViewModel() = HistoryViewModel(clothingItemDataSource, outfitDataSource)

    private fun testOutfit(status: OutfitStatus, createdAt: Long, favorite: Boolean = false) = Outfit(
        topItemId = 1L,
        bottomItemId = 2L,
        shoesItemId = 3L,
        accessoryItemId = null,
        occasion = null,
        status = status,
        favorite = favorite,
        createdAt = createdAt,
    )

    @Test
    fun `history shows resolved outfits, most recent first`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        outfitDataSource.upsert(testOutfit(OutfitStatus.ACCEPTED, createdAt = 100L))
        outfitDataSource.upsert(testOutfit(OutfitStatus.REJECTED, createdAt = 200L))
        val viewModel = buildViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.outfits).hasSize(2)
            assertThat(state.outfits[0].status).isEqualTo(OutfitStatus.REJECTED)
            assertThat(state.outfits[1].status).isEqualTo(OutfitStatus.ACCEPTED)
            assertThat(state.outfits[0].top.type).isEqualTo(ClothingType.TOP)
            assertThat(state.outfits[0].bottom.type).isEqualTo(ClothingType.BOTTOM)
            assertThat(state.outfits[0].shoes.type).isEqualTo(ClothingType.SHOES)
        }
    }

    @Test
    fun `toggling favorite in history persists the change`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        outfitDataSource.upsert(testOutfit(OutfitStatus.ACCEPTED, createdAt = 100L))
        val viewModel = buildViewModel()

        viewModel.state.test {
            val outfitId = awaitItem().outfits.first().outfitId // initial

            viewModel.onAction(HistoryAction.OnFavoriteToggle(outfitId))

            assertThat(awaitItem().outfits.first().favorite).isTrue()
        }
        assertThat(outfitDataSource.all.first().favorite).isTrue()
    }

    @Test
    fun `a failed favorite toggle shows an error`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        outfitDataSource.upsert(testOutfit(OutfitStatus.ACCEPTED, createdAt = 100L))
        outfitDataSource.setFavoriteError = DataError.Local.DISK_FULL
        val viewModel = buildViewModel()
        val outfitId = viewModel.state.value.outfits.first().outfitId

        viewModel.events.test {
            viewModel.onAction(HistoryAction.OnFavoriteToggle(outfitId))
            assertThat(awaitItem()).isEqualTo(HistoryEvent.ShowError(DataError.Local.DISK_FULL))
        }
    }

    private suspend fun seedWardrobe() {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
    }

    @Test
    fun `the favourites filter hides everything else`() = runTest {
        seedWardrobe()
        outfitDataSource.upsert(testOutfit(OutfitStatus.ACCEPTED, createdAt = 100L, favorite = false))
        outfitDataSource.upsert(testOutfit(OutfitStatus.ACCEPTED, createdAt = 200L, favorite = true))
        val viewModel = buildViewModel()

        viewModel.state.test {
            assertThat(awaitItem().visibleOutfits).hasSize(2)

            viewModel.onAction(HistoryAction.OnFavoritesOnlyToggle)
            val filtered = awaitItem()
            assertThat(filtered.visibleOutfits).hasSize(1)
            assertThat(filtered.visibleOutfits.first().favorite).isTrue()

            viewModel.onAction(HistoryAction.OnFavoritesOnlyToggle)
            assertThat(awaitItem().visibleOutfits).hasSize(2)
        }
    }

    @Test
    fun `a resolved outfit carries the date it was created`() = runTest {
        seedWardrobe()
        outfitDataSource.upsert(testOutfit(OutfitStatus.ACCEPTED, createdAt = 1_700_000_000_000L))
        val viewModel = buildViewModel()

        viewModel.state.test {
            assertThat(awaitItem().outfits.single().createdAt).isEqualTo(1_700_000_000_000L)
        }
    }
}
