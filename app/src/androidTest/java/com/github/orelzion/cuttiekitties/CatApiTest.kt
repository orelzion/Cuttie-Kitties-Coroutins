package com.github.orelzion.cuttiekitties

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.orelzion.cuttiekitties.model.network.catApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatApiTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    @Throws(Exception::class)
    fun testGettingBreadList() = runBlocking {
        val breedList = catApi.fetchBreedsList(1, 0)
        assert(breedList.size == 1)
    }

    @Test
    @Throws(Exception::class)
    fun testGettingImagesByBread() = runBlocking {
        val imageList = catApi.fetchImagesByBreed("abys", 4, 1)
        assert(imageList.size == 4)
    }
}