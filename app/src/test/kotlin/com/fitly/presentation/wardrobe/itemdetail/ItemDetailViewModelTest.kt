package com.fitly.presentation.wardrobe.itemdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result
import com.fitly.fakes.FakeClothingItemLocalDataSource
import com.fitly.testutil.MainDispatcherExtension
import com.fitly.testutil.testClothingItem
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ItemDetailViewModelTest {

    companion object {
        @JvmField
        @RegisterExtension
        val mainDispatcherExtension = MainDispatcherExtension()
    }

    private val clothingItemDataSource = FakeClothingItemLocalDataSource()

    private fun viewModelFor(itemId: Long) =
        ItemDetailViewModel(SavedStateHandle(mapOf("itemId" to itemId)), clothingItemDataSource)

    @Test
    fun `loading an existing item populates state with its fields`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success

        val viewModel = viewModelFor(saved.data)

        viewModel.state.test {
            val loaded = awaitItem()
            assertThat(loaded.photoPath).isEqualTo("/photos/TOP.jpg")
            assertThat(loaded.type).isEqualTo(ClothingType.TOP)
            assertThat(loaded.occasion).isEqualTo(Occasion.CASUAL)
            assertThat(loaded.season).isEqualTo(Season.ALL_YEAR)
            assertThat(loaded.condition).isEqualTo(Condition.NEW)
        }
    }

    @Test
    fun `loading an id that was never saved marks state as not found`() = runTest {
        val viewModel = viewModelFor(itemId = 999)

        viewModel.state.test {
            assertThat(awaitItem().isNotFound).isTrue()
        }
    }

    @Test
    fun `changing each tag field updates its own state field independently`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)

        viewModel.state.test {
            awaitItem() // loaded

            viewModel.onAction(ItemDetailAction.OnTypeChanged(ClothingType.DRESS))
            assertThat(awaitItem().type).isEqualTo(ClothingType.DRESS)

            viewModel.onAction(ItemDetailAction.OnOccasionChanged(Occasion.FORMAL))
            assertThat(awaitItem().occasion).isEqualTo(Occasion.FORMAL)

            viewModel.onAction(ItemDetailAction.OnSeasonChanged(Season.WINTER))
            assertThat(awaitItem().season).isEqualTo(Season.WINTER)

            viewModel.onAction(ItemDetailAction.OnConditionChanged(Condition.WORN))
            assertThat(awaitItem().condition).isEqualTo(Condition.WORN)
        }
    }

    @Test
    fun `save click persists the edited tags and emits Saved`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)
        viewModel.onAction(ItemDetailAction.OnConditionChanged(Condition.WORN))

        viewModel.events.test {
            viewModel.onAction(ItemDetailAction.OnSaveClick)
            assertThat(awaitItem()).isEqualTo(ItemDetailEvent.Saved)
        }
        val persisted = (clothingItemDataSource.getById(saved.data) as Result.Success).data
        assertThat(persisted.condition).isEqualTo(Condition.WORN)
    }

    @Test
    fun `a second save click while the first save is still in flight is ignored`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)
        viewModel.onAction(ItemDetailAction.OnConditionChanged(Condition.WORN))
        val gate = CompletableDeferred<Unit>()
        clothingItemDataSource.upsertGate = gate

        viewModel.events.test {
            viewModel.onAction(ItemDetailAction.OnSaveClick) // starts saving, suspends on the gate
            viewModel.onAction(ItemDetailAction.OnSaveClick) // should be ignored: a save is already in flight
            gate.complete(Unit)

            assertThat(awaitItem()).isEqualTo(ItemDetailEvent.Saved)
            expectNoEvents()
        }
    }

    @Test
    fun `a second confirm-delete click while the first delete is still in flight is ignored`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)
        viewModel.onAction(ItemDetailAction.OnDeleteClick)
        val gate = CompletableDeferred<Unit>()
        clothingItemDataSource.deleteGate = gate

        viewModel.events.test {
            viewModel.onAction(ItemDetailAction.OnConfirmDeleteClick) // starts deleting, suspends on the gate
            viewModel.onAction(ItemDetailAction.OnConfirmDeleteClick) // should be ignored
            gate.complete(Unit)

            assertThat(awaitItem()).isEqualTo(ItemDetailEvent.Deleted)
            expectNoEvents()
        }
    }

    @Test
    fun `delete click shows the delete confirmation`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)

        viewModel.state.test {
            awaitItem() // loaded

            viewModel.onAction(ItemDetailAction.OnDeleteClick)

            assertThat(awaitItem().isDeleteConfirmationVisible).isTrue()
        }
    }

    @Test
    fun `cancelling delete hides the confirmation without deleting anything`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)
        viewModel.onAction(ItemDetailAction.OnDeleteClick)

        viewModel.state.test {
            awaitItem() // confirmation visible

            viewModel.onAction(ItemDetailAction.OnCancelDeleteClick)

            assertThat(awaitItem().isDeleteConfirmationVisible).isEqualTo(false)
        }
        assertThat(clothingItemDataSource.getById(saved.data)).isEqualTo(Result.Success(testClothingItem().copy(id = saved.data)))
    }

    @Test
    fun `confirming delete removes the item and emits Deleted`() = runTest {
        val saved = clothingItemDataSource.upsert(testClothingItem()) as Result.Success
        val viewModel = viewModelFor(saved.data)
        viewModel.onAction(ItemDetailAction.OnDeleteClick)

        viewModel.events.test {
            viewModel.onAction(ItemDetailAction.OnConfirmDeleteClick)
            assertThat(awaitItem()).isEqualTo(ItemDetailEvent.Deleted)
        }
        assertThat(clothingItemDataSource.getById(saved.data)).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }
}
