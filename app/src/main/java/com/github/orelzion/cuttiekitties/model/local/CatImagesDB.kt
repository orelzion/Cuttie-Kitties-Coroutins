package com.github.orelzion.cuttiekitties.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.local.entity.CatImage

@Database(entities = [CatImage::class, Breed::class], version = 1, exportSchema = true)
abstract class CatImagesDB : RoomDatabase() {
    abstract val catImagesDao: CatImagesDao
}

private lateinit var INSTANCE: CatImagesDB

fun getDatabase(context: Context): CatImagesDB {
    synchronized(CatImagesDB::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    CatImagesDB::class.java,
                    "cat_images_db"
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}