package com.github.orelzion.cuttiekitties.model.repository

import androidx.paging.toLiveData
import com.github.orelzion.cuttiekitties.model.local.CatImagesDao
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.local.entity.CatImage
import com.github.orelzion.cuttiekitties.model.network.CatApi
import kotlinx.coroutines.CoroutineScope

class CatRepository(private val catApi: CatApi, private val catImagesDao: CatImagesDao) {

    fun getBreedListLiveData(coroutineScope: CoroutineScope) =
        catImagesDao.getAllBreeds().toLiveData(
            pageSize = CatApi.PAGE_SIZE,
            boundaryCallback = BreedsBoundaryCallback(coroutineScope, repository = this)
        )

    fun observeImagesList(breed: Breed) = catImagesDao.getAllImagesByBreed(breed.id)

    suspend fun refreshBreeds() {
        // Get how many breeds we already have, so we can page request more
        val breedsDownloaded = catImagesDao.getBreedsCount()

        // Get breed list from the API
        val breedsList = catApi.fetchBreedsList(CatApi.PAGE_SIZE, breedsDownloaded.toPageSize())

        // Convert network breed to local breed
        breedsList.map { it.toLocalBreed() }.let {
            // Save breed list to local DB
            catImagesDao.insertBreeds(it)
        }
    }

    suspend fun refreshCatImages(breed: Breed) {
        // Get how many images we already downloaded by this breed id, so we can page request more
        val imagesDownloaded = catImagesDao.getImageCountByBreed(breed.id)

        // Get images list by breed id from the API
        val imagesList = catApi.fetchImagesByBreed(
            breed_id = breed.id,
            limit = CatApi.PAGE_SIZE,
            page = imagesDownloaded.toPageSize()
        )

        // Convert network image object to local object
        imagesList.map { it.toLocalCatImage() }.let {
            // Save images list to local DB
            catImagesDao.insertImages(it)
        }
    }

    private fun com.github.orelzion.cuttiekitties.model.network.entity.Breed.toLocalBreed(): Breed {
        return Breed(this.name, this.alt_names, this.id)
    }

    private fun com.github.orelzion.cuttiekitties.model.network.entity.CatImage.toLocalCatImage(): CatImage {
        return CatImage(this.id, this.url, this.breeds.first().id)
    }

    private fun Int.toPageSize() = this / CatApi.PAGE_SIZE

}