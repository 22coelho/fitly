package com.fitly.data.util

import android.database.sqlite.SQLiteFullException
import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result
import kotlinx.coroutines.CancellationException

suspend inline fun <T> safeRoomCall(action: () -> T): Result<T, DataError.Local> {
    return try {
        Result.Success(action())
    } catch (e: CancellationException) {
        throw e
    } catch (e: SQLiteFullException) {
        Result.Error(DataError.Local.DISK_FULL)
    } catch (e: Exception) {
        Result.Error(DataError.Local.UNKNOWN)
    }
}
