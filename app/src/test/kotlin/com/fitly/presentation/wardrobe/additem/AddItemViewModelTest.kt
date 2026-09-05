package com.fitly.presentation.wardrobe.additem

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.domain.util.DataError
import com.fitly.fakes.FakeClothingItemLocalDataSource
import com.fitly.fakes.FakeDominantColorExtractor
import com.fitly.fakes.FakePhotoLocalDataSource
import com.fitly.testutil.MainDispatcherExtension
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AddItemViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val colorExtractor = FakeDominantColorExtractor(colorToReturn = 0xFF0000)
    private val photoDataSource = FakePhotoLocalDataSource(pathToReturn = "/photos/1.jpg")
    private val clothingItemDataSource = FakeClothingItemLocalDataSource()

    private val viewModel = AddItemViewModel(colorExtractor, photoDataSource, clothingItemDataSource)

    @Test
    fun `capturing a photo extracts its color and saves it, updating state`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(AddItemState())

            viewModel.onAction(AddItemAction.OnPhotoCaptured(byteArrayOf(1, 2, 3)))

            assertThat(awaitItem().status).isEqualTo(AddItemStatus.PROCESSING_PHOTO)
            val updated = awaitItem()
            assertThat(updated.photoPath).isEqualTo("/photos/1.jpg")
            assertThat(updated.dominantColor).isEqualTo(0xFF0000)
            assertThat(updated.status).isEqualTo(AddItemStatus.IDLE)
        }
    }

    @Test
    fun `capturing a photo when saving it fails emits ShowError`() = runTest {
        photoDataSource.errorToReturn = DataError.Local.DISK_FULL

        viewModel.events.test {
            viewModel.onAction(AddItemAction.OnPhotoCaptured(byteArrayOf(1, 2, 3)))
            assertThat(awaitItem()).isEqualTo(AddItemEvent.ShowError(DataError.Local.DISK_FULL))
        }
    }

    @Test
    fun `selecting each tag updates its own state field independently`() = runTest {
        viewModel.state.test {
            awaitItem() // initial

            viewModel.onAction(AddItemAction.OnTypeSelected(ClothingType.TOP))
            assertThat(awaitItem().type).isEqualTo(ClothingType.TOP)

            viewModel.onAction(AddItemAction.OnOccasionSelected(Occasion.CASUAL))
            assertThat(awaitItem().occasion).isEqualTo(Occasion.CASUAL)

            viewModel.onAction(AddItemAction.OnSeasonSelected(Season.ALL_YEAR))
            assertThat(awaitItem().season).isEqualTo(Season.ALL_YEAR)

            viewModel.onAction(AddItemAction.OnConditionSelected(Condition.NEW))
            assertThat(awaitItem().condition).isEqualTo(Condition.NEW)
        }
    }

    @Test
    fun `save click before every field is chosen does nothing`() = runTest {
        viewModel.onAction(AddItemAction.OnTypeSelected(ClothingType.TOP))
        // occasion, season, condition and a photo are still missing.

        viewModel.events.test {
            viewModel.onAction(AddItemAction.OnSaveClick)
            expectNoEvents()
        }
        assertThat(clothingItemDataSource.observeAll().value).isEqualTo(emptyList())
    }

    @Test
    fun `save click with every field chosen saves the item and emits ItemSaved`() = runTest {
        selectAllTagsAndCapturePhoto()

        viewModel.events.test {
            viewModel.onAction(AddItemAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(AddItemEvent.ItemSaved)
        }
        assertThat(clothingItemDataSource.observeAll().value.single().photoPath).isEqualTo("/photos/1.jpg")
    }

    @Test
    fun `a second save click while the first save is still in flight is ignored`() = runTest {
        val gate = CompletableDeferred<Unit>()
        clothingItemDataSource.upsertGate = gate
        selectAllTagsAndCapturePhoto()

        viewModel.onAction(AddItemAction.OnSaveClick) // starts saving, suspends on the gate
        viewModel.onAction(AddItemAction.OnSaveClick) // should be ignored: a save is already in flight
        gate.complete(Unit) // let the first save proceed

        assertThat(clothingItemDataSource.observeAll().value).hasSize(1)
    }

    @Test
    fun `save click when saving fails emits ShowError`() = runTest {
        clothingItemDataSource.upsertError = DataError.Local.UNKNOWN
        selectAllTagsAndCapturePhoto()

        viewModel.events.test {
            viewModel.onAction(AddItemAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(AddItemEvent.ShowError(DataError.Local.UNKNOWN))
        }
    }

    /** Captures a photo and picks every tag, leaving the form ready for OnSaveClick. */
    private fun selectAllTagsAndCapturePhoto() {
        viewModel.onAction(AddItemAction.OnPhotoCaptured(byteArrayOf(1, 2, 3)))
        viewModel.onAction(AddItemAction.OnTypeSelected(ClothingType.TOP))
        viewModel.onAction(AddItemAction.OnOccasionSelected(Occasion.CASUAL))
        viewModel.onAction(AddItemAction.OnSeasonSelected(Season.ALL_YEAR))
        viewModel.onAction(AddItemAction.OnConditionSelected(Condition.NEW))
    }
}
