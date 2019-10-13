package com.example.ugurozmen.imagelist.model

import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val photoService: PhotoService) {
    suspend fun getPhotos(): List<Photo> = withContext(Dispatchers.IO) {
        photoService.getPhotos()
    }
}

@JsonClass(generateAdapter = true)
data class Photo(
    val albumId: Long,
    val id: Long,
    val title: String,
    val url: String,
    val thumbnailUrl: String
)

interface PhotoService {
    @GET("/photos")
    suspend fun getPhotos(): List<Photo>
}