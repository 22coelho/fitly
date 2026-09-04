package com.fitly.data.datasource

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.fitly.data.database.FitlyDatabase
import com.fitly.domain.model.ClothingItem
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Season
import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Plain Application: this is a pure Room integration test, it never touches Koin/DI.
@Config(application = Application::class)
@RunWith(RobolectricTestRunner::class)
class RoomClothingItemDataSourceTest {

    private lateinit var database: FitlyDatabase
    private lateinit var dataSource: RoomClothingItemDataSource

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FitlyDatabase::class.java,
        ).allowMainThreadQueries().build()
        dataSource = RoomClothingItemDataSource(database.clothingItemDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `upsert then observeAll emits the saved item`() = runTest {
        val item = testItem()

        dataSource.upsert(item)

        dataSource.observeAll().test {
            val items = awaitItem()
            assertThat(items).hasSize(1)
            assertThat(items.first().photoPath).isEqualTo(item.photoPath)
        }
    }

    @Test
    fun `getById for a saved item returns it`() = runTest {
        val saved = dataSource.upsert(testItem()) as Result.Success

        val result = dataSource.getById(saved.data) as Result.Success

        assertThat(result.data.photoPath).isEqualTo(testItem().photoPath)
    }

    @Test
    fun `getById for an id that was never saved returns NOT_FOUND`() = runTest {
        val result = dataSource.getById(id = 999)

        assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `upsert on an already-saved item updates it and keeps its id`() = runTest {
        val saved = dataSource.upsert(testItem()) as Result.Success
        val existing = (dataSource.getById(saved.data) as Result.Success).data

        val updated = dataSource.upsert(existing.copy(condition = Condition.WORN))

        assertThat(updated).isEqualTo(Result.Success(saved.data))
        val result = dataSource.getById(saved.data) as Result.Success
        assertThat(result.data.condition).isEqualTo(Condition.WORN)
        dataSource.observeAll().test {
            assertThat(awaitItem()).hasSize(1)
        }
    }

    @Test
    fun `delete removes the item`() = runTest {
        val saved = dataSource.upsert(testItem()) as Result.Success

        dataSource.delete(saved.data)

        dataSource.observeAll().test {
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun `delete for an id that was never saved returns NOT_FOUND`() = runTest {
        val result = dataSource.delete(id = 999)

        assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    private fun testItem() = ClothingItem(
        photoPath = "/photos/1.jpg",
        dominantColor = 0xFFFF0000.toInt(),
        type = ClothingType.TOP,
        occasion = Occasion.CASUAL,
        season = Season.ALL_YEAR,
        condition = Condition.NEW,
        createdAt = 0L,
    )
}
