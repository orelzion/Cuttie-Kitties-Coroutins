package com.github.orelzion.cuttiekitties.model.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.local.entity.CatImage

@Dao
interface CatImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreeds(breads: List<Breed>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<CatImage>)

    @Query("select * from Breed")
    fun getAllBreeds(): DataSource.Factory<Int, Breed>

    @Query("select count(*) from Breed")
    suspend fun getBreedsCount(): Int

    @Query("select * from CatImage where breed_id = :breedId")
    fun getAllImagesByBreed(breedId: String): LiveData<List<CatImage>>

    @Query("select count(*) from CatImage where breed_id = :breedId")
    suspend fun getImageCountByBreed(breedId: String): Int
}