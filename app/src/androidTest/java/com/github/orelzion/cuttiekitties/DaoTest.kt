package com.github.orelzion.cuttiekitties

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.paging.LimitOffsetDataSource
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.orelzion.cuttiekitties.model.local.CatImagesDB
import com.github.orelzion.cuttiekitties.model.local.CatImagesDao
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.local.entity.CatImage
import com.jraska.livedata.test
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.io.IOException
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DaoTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var catDao: CatImagesDao
    private lateinit var db: CatImagesDB

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, CatImagesDB::class.java
        ).build()
        catDao = db.catImagesDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeBreedAndReadInList() = runBlockingTest {
        val breed = Breed(name = "some cat", id = "2", alt_names = null)

        catDao.insertBreeds(listOf(breed))

        val factory = catDao.getAllBreeds()

        val list = (factory.create() as LimitOffsetDataSource).loadRange(0, 10)
        assertThat(list.size, "list.size to be ").isEqualTo(1)
        assertThat(catDao.getBreedsCount(), "breeds count to be").isEqualTo(1)
    }

    @Test
    @Throws(Exception::class)
    fun writeBreedImagesAndReadInList() = runBlockingTest {
        val image = CatImage(id = "1", url = "url", breed_id = "2")
        val anotherImage = CatImage(id = "2", url = "url", breed_id = "1")

        catDao.insertImages(listOf(image, anotherImage))

        catDao.getAllImagesByBreed("2")
            .test()
            .assertValue { it.size == 1 }
    }


    @Test
    fun testImagesCountByBreed() = runBlockingTest {
        val image = CatImage(id = "1", url = "url", breed_id = "2")
        val anotherImage = CatImage(id = "2", url = "url", breed_id = "1")

        catDao.insertImages(listOf(image, anotherImage))

        val imagesCount = catDao.getImageCountByBreed("2")
        assertThat(imagesCount, "images count to be").isEqualTo(1)
    }
}