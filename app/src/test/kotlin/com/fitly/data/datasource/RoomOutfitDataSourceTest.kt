package com.fitly.data.datasource

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.fitly.data.database.ClothingItemEntity
import com.fitly.data.database.FitlyDatabase
import com.fitly.domain.model.ClothingType
import com.fitly.domain.model.Condition
import com.fitly.domain.model.Occasion
import com.fitly.domain.model.Outfit
import com.fitly.domain.model.OutfitStatus
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
class RoomOutfitDataSourceTest {

    private lateinit var database: FitlyDatabase
    private lateinit var dataSource: RoomOutfitDataSource
    private var topId: Long = 0
    private var bottomId: Long = 0
    private var shoesId: Long = 0

    @Before
    fun setUp() = runTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FitlyDatabase::class.java,
        ).allowMainThreadQueries().build()
        dataSource = RoomOutfitDataSource(database.outfitDao())

        // Outfit item-id columns are foreign keys into clothing_items, so every
        // outfit fixture needs real, already-saved ClothingItem rows to point at.
        val clothingItemDao = database.clothingItemDao()
        topId = clothingItemDao.upsert(testClothingItem(ClothingType.TOP))
        bottomId = clothingItemDao.upsert(testClothingItem(ClothingType.BOTTOM))
        shoesId = clothingItemDao.upsert(testClothingItem(ClothingType.SHOES))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `upsert then getById returns the saved outfit`() = runTest {
        val saved = dataSource.upsert(testOutfit(status = OutfitStatus.PENDING)) as Result.Success

        val result = dataSource.getById(saved.data) as Result.Success

        assertThat(result.data.status).isEqualTo(OutfitStatus.PENDING)
    }

    @Test
    fun `getById for an id that was never saved returns NOT_FOUND`() = runTest {
        val result = dataSource.getById(id = 999)

        assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `upsert on an already-saved outfit updates it and keeps its id`() = runTest {
        val saved = dataSource.upsert(testOutfit(status = OutfitStatus.PENDING)) as Result.Success
        val existing = (dataSource.getById(saved.data) as Result.Success).data

        val updated = dataSource.upsert(existing.copy(favorite = true))

        assertThat(updated).isEqualTo(Result.Success(saved.data))
        val result = dataSource.getById(saved.data) as Result.Success
        assertThat(result.data.favorite).isTrue()
    }

    @Test
    fun `observeHistory excludes PENDING and includes ACCEPTED and REJECTED`() = runTest {
        dataSource.upsert(testOutfit(status = OutfitStatus.PENDING))
        val accepted = dataSource.upsert(testOutfit(status = OutfitStatus.ACCEPTED)) as Result.Success
        val rejected = dataSource.upsert(testOutfit(status = OutfitStatus.REJECTED)) as Result.Success

        dataSource.observeHistory().test {
            val history = awaitItem()
            assertThat(history.map { it.id }).containsExactlyInAnyOrder(accepted.data, rejected.data)
        }
    }

    @Test
    fun `observeHistory is empty when every outfit is still PENDING`() = runTest {
        dataSource.upsert(testOutfit(status = OutfitStatus.PENDING))

        dataSource.observeHistory().test {
            assertThat(awaitItem()).isEmpty()
        }
    }

    @Test
    fun `setStatus updates the outfit's status`() = runTest {
        val saved = dataSource.upsert(testOutfit(status = OutfitStatus.PENDING)) as Result.Success

        dataSource.setStatus(saved.data, OutfitStatus.ACCEPTED)

        val result = dataSource.getById(saved.data) as Result.Success
        assertThat(result.data.status).isEqualTo(OutfitStatus.ACCEPTED)
    }

    @Test
    fun `setStatus for an id that was never saved returns NOT_FOUND`() = runTest {
        val result = dataSource.setStatus(id = 999, status = OutfitStatus.ACCEPTED)

        assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    @Test
    fun `setFavorite marks the outfit as favorite`() = runTest {
        val saved = dataSource.upsert(testOutfit(status = OutfitStatus.ACCEPTED, favorite = false)) as Result.Success

        dataSource.setFavorite(saved.data, true)

        val result = dataSource.getById(saved.data) as Result.Success
        assertThat(result.data.favorite).isTrue()
    }

    @Test
    fun `setFavorite for an id that was never saved returns NOT_FOUND`() = runTest {
        val result = dataSource.setFavorite(id = 999, favorite = true)

        assertThat(result).isEqualTo(Result.Error(DataError.Local.NOT_FOUND))
    }

    private fun testOutfit(status: OutfitStatus, favorite: Boolean = false) = Outfit(
        topItemId = topId,
        bottomItemId = bottomId,
        shoesItemId = shoesId,
        accessoryItemId = null,
        occasion = Occasion.CASUAL,
        status = status,
        favorite = favorite,
        createdAt = 0L,
    )

    private fun testClothingItem(type: ClothingType) = ClothingItemEntity(
        photoPath = "/photos/$type.jpg",
        dominantColor = 0xFFFF0000.toInt(),
        type = type,
        occasion = Occasion.CASUAL,
        season = Season.ALL_YEAR,
        condition = Condition.NEW,
        createdAt = 0L,
    )
}
