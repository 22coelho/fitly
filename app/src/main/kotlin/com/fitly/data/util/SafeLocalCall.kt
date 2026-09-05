package com.fitly.data.util

import android.database.sqlite.SQLiteFullException
import com.fitly.domain.util.DataError
import com.fitly.domain.util.EmptyResult
import com.fitly.domain.util.Result
import kotlinx.coroutines.CancellationException

suspend inline fun <T> safeLocalCall(action: () -> T): Result<T, DataError.Local> {
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

/** Maps a DAO's affected-row count to NOT_FOUND when nothing matched the WHERE clause. */
fun Result<Int, DataError.Local>.toEmptyResultOrNotFound(): EmptyResult<DataError.Local> =
    when (this) {
        is Result.Error -> this
        is Result.Success -> if (data == 0) {
            Result.Error(DataError.Local.NOT_FOUND)
        } else {
            Result.Success(Unit)
        }
    }
