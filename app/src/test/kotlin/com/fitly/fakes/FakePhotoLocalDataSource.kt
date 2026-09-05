package com.fitly.fakes

import com.fitly.domain.datasource.PhotoLocalDataSource
import com.fitly.domain.util.DataError
import com.fitly.domain.util.Result

class FakePhotoLocalDataSource(
    var pathToReturn: String = "/fake/photo.jpg",
    var errorToReturn: DataError.Local? = null,
) : PhotoLocalDataSource {
    override suspend fun save(photoBytes: ByteArray): Result<String, DataError.Local> {
        return errorToReturn?.let { Result.Error(it) } ?: Result.Success(pathToReturn)
    }
}
