package com.github.orelzion.cuttiekitties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.room.paging.LimitOffsetDataSource
import com.github.orelzion.cuttiekitties.model.local.CatImagesDao
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.local.entity.CatImage
import com.github.orelzion.cuttiekitties.model.network.CatApi
import com.github.orelzion.cuttiekitties.model.network.entity.BreedResponse
import com.github.orelzion.cuttiekitties.model.network.entity.CatImageResponse
import io.mockk.mockk

typealias NetworkBreed = com.github.orelzion.cuttiekitties.model.network.entity.Breed
typealias NetworkCatImage = com.github.orelzion.cuttiekitties.model.network.entity.CatImage

class CatDaoFake : CatImagesDao {

    private val breedsLiveData = MutableLiveData<List<Breed>>()
    private val imagesLiveData = MutableLiveData<List<CatImage>>()

    override suspend fun insertBreeds(breads: List<Breed>) {
        breedsLiveData.value = breads
    }

    override suspend fun insertImages(images: List<CatImage>) {
        imagesLiveData.value = images
    }

    override fun getAllBreeds(): DataSource.Factory<Int, Breed> {
        return object : DataSource.Factory<Int, Breed>() {
            override fun create(): LimitOffsetDataSource<Breed> {
                return MockLimitDataSource(breedsLiveData.value!!)
            }
        }
    }

    override suspend fun getBreedsCount(): Int {
        return breedsLiveData.value?.size ?: 0
    }

    @Throws(NullPointerException::class)
    override fun getAllImagesByBreed(breedId: String): LiveData<List<CatImage>> {
        val filteredList = imagesLiveData.value!!.filter { it.breed_id == breedId }
        return MutableLiveData<List<CatImage>>().apply { value = filteredList }
    }

    override suspend fun getImageCountByBreed(breedId: String): Int {
        return imagesLiveData.value?.size ?: 0
    }
}

class CatApiFake(
    private val breedList: List<NetworkBreed> = listOf(
        mockk(relaxed = true),
        mockk(relaxed = true)
    ),
    private
    val imagesList: List<NetworkCatImage> = listOf(
        mockk(relaxed = true)
    )
) : CatApi {
    override suspend fun fetchImagesByBreed(
        breed_id: String,
        limit: Int,
        page: Int
    ): CatImageResponse {
        return imagesList
    }

    override suspend fun fetchBreedsList(limit: Int, page: Int): BreedResponse {
        return breedList
    }
}