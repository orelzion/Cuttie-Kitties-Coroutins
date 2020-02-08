package com.github.orelzion.cuttiekitties

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.orelzion.cuttiekitties.model.network.entity.Breed
import com.github.orelzion.cuttiekitties.model.network.entity.CatImage
import com.github.orelzion.cuttiekitties.model.repository.CatRepository
import com.jraska.livedata.test
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test


lateinit var repository: CatRepository

class CatRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `refresh breeds from network and save to local DB`() = runBlockingTest {
        // Given
        repository = CatRepository(CatApiFake(), CatDaoFake())

        // When
        repository.refreshBreeds()

        // Then
        repository.getBreedListLiveData(this)
            .test()
            .assertHasValue()
            .assertValue { it.size == 2 }
    }

    @Test
    fun `refresh images list by breed id and save to local DB`() = runBlockingTest {
        //Given
        val fakeLocalBreed =
            com.github.orelzion.cuttiekitties.model.local.entity.Breed(name = "name", id = "1")
        val fakeNetworkBreed = Breed(name = "name", id = "1")
        val fakeImage = CatImage(id = "1", url = "url", breeds = listOf(fakeNetworkBreed))

        repository = CatRepository(
            CatApiFake(breedList = listOf(fakeNetworkBreed), imagesList = listOf(fakeImage)),
            CatDaoFake()
        )

        // When
        repository.refreshCatImages(fakeLocalBreed)

        // Then
        repository.observeImagesList(fakeLocalBreed)
            .test()
            .assertHasValue()
            .assertValue { it.size == 1 }
    }
}