package com.fitly.presentation.home

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.fitly.domain.generator.OutfitGenerator
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.OutfitStatus
import com.fitly.domain.util.DataError
import com.fitly.fakes.FakeClothingItemLocalDataSource
import com.fitly.fakes.FakeOutfitLocalDataSource
import com.fitly.testutil.MainDispatcherExtension
import com.fitly.testutil.testClothingItem
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class HomeViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val clothingItemDataSource = FakeClothingItemLocalDataSource()
    private val outfitDataSource = FakeOutfitLocalDataSource()

    private fun buildViewModel() = HomeViewModel(clothingItemDataSource, outfitDataSource, OutfitGenerator())

    @Test
    fun `generating an outfit saves it and shows the resolved items`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)

            assertThat(awaitItem().status).isEqualTo(HomeStatus.GENERATING)
            val generated = awaitItem()
            val outfit = generated.outfit
            assertThat(outfit).isNotNull()
            assertThat(outfit?.top?.type).isEqualTo(ClothingType.TOP)
            assertThat(outfit?.bottom?.type).isEqualTo(ClothingType.BOTTOM)
            assertThat(outfit?.shoes?.type).isEqualTo(ClothingType.SHOES)
            assertThat(generated.status).isEqualTo(HomeStatus.IDLE)
        }
        assertThat(outfitDataSource.all).hasSize(1)
    }

    @Test
    fun `accepting an outfit persists the accepted status and clears it from the screen`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        val viewModel = buildViewModel()
        var generatedId: Long? = null

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)
            awaitItem() // GENERATING
            generatedId = awaitItem().outfit?.outfitId

            viewModel.onAction(HomeAction.OnAcceptClick)

            assertThat(awaitItem().status).isEqualTo(HomeStatus.SAVING)
            val afterAccept = awaitItem()
            assertThat(afterAccept.status).isEqualTo(HomeStatus.IDLE)
            assertThat(afterAccept.outfit).isNull()
        }
        val saved = outfitDataSource.all.find { it.id == generatedId }
        assertThat(saved?.status).isEqualTo(OutfitStatus.ACCEPTED)
    }

    @Test
    fun `rejecting an outfit persists the rejected status and clears it from the screen`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        val viewModel = buildViewModel()
        var generatedId: Long? = null

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)
            awaitItem() // GENERATING
            generatedId = awaitItem().outfit?.outfitId

            viewModel.onAction(HomeAction.OnRejectClick)

            assertThat(awaitItem().status).isEqualTo(HomeStatus.SAVING)
            val afterReject = awaitItem()
            assertThat(afterReject.status).isEqualTo(HomeStatus.IDLE)
            assertThat(afterReject.outfit).isNull()
        }
        val saved = outfitDataSource.all.find { it.id == generatedId }
        assertThat(saved?.status).isEqualTo(OutfitStatus.REJECTED)
    }

    @Test
    fun `toggling favorite flips the flag and keeps the outfit on screen`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)
            awaitItem() // GENERATING
            val beforeToggle = awaitItem()
            assertThat(beforeToggle.outfit?.favorite).isEqualTo(false)

            viewModel.onAction(HomeAction.OnFavoriteToggle)

            assertThat(awaitItem().status).isEqualTo(HomeStatus.SAVING)
            val afterToggle = awaitItem()
            assertThat(afterToggle.status).isEqualTo(HomeStatus.IDLE)
            assertThat(afterToggle.outfit?.favorite).isEqualTo(true)
        }
    }

    @Test
    fun `selecting an occasion filter updates state and is used by the next generation`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP, occasion = Occasion.FORMAL))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM, occasion = Occasion.FORMAL))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES, occasion = Occasion.FORMAL))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP, occasion = Occasion.CASUAL))
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnOccasionFilterSelected(Occasion.FORMAL))
            assertThat(awaitItem().occasionFilter).isEqualTo(Occasion.FORMAL)

            viewModel.onAction(HomeAction.OnGenerateClick)
            awaitItem() // GENERATING
            val generated = awaitItem()
            assertThat(generated.outfit?.top?.occasion).isEqualTo(Occasion.FORMAL)
        }
    }

    @Test
    fun `a second generate click while generating is ignored`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        outfitDataSource.upsertGate = CompletableDeferred()
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)
            assertThat(awaitItem().status).isEqualTo(HomeStatus.GENERATING)

            viewModel.onAction(HomeAction.OnGenerateClick)

            outfitDataSource.upsertGate?.complete(Unit)
            val settled = awaitItem()
            assertThat(settled.status).isEqualTo(HomeStatus.IDLE)
        }
        assertThat(outfitDataSource.all).hasSize(1)
    }

    @Test
    fun `generating when the wardrobe can't cover every slot marks noItemsAvailable`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)

            assertThat(awaitItem().noItemsAvailable).isTrue()
        }
    }

    @Test
    fun `completing the wardrobe and generating again clears noItemsAvailable`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        val viewModel = buildViewModel()

        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(HomeAction.OnGenerateClick)
            assertThat(awaitItem().noItemsAvailable).isTrue()

            clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
            clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
            viewModel.onAction(HomeAction.OnGenerateClick)

            assertThat(awaitItem().status).isEqualTo(HomeStatus.GENERATING)
            val generated = awaitItem()
            assertThat(generated.noItemsAvailable).isFalse()
            assertThat(generated.outfit).isNotNull()
        }
    }

    @Test
    fun `a failed save shows an error and returns to idle`() = runTest {
        clothingItemDataSource.upsert(testClothingItem(ClothingType.TOP))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.BOTTOM))
        clothingItemDataSource.upsert(testClothingItem(ClothingType.SHOES))
        outfitDataSource.upsertError = DataError.Local.DISK_FULL
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.onAction(HomeAction.OnGenerateClick)
            assertThat(awaitItem()).isEqualTo(HomeEvent.ShowError(DataError.Local.DISK_FULL))
        }
        assertThat(viewModel.state.value.status).isEqualTo(HomeStatus.IDLE)
        assertThat(viewModel.state.value.outfit).isNull()
    }
}
