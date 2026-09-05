package com.fitly.domain.datasource

import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result

interface PhotoLocalDataSource {
    /** Compresses and saves the photo, returning its local file path. */
    suspend fun save(photoBytes: ByteArray): Result<String, DataError.Local>
}
