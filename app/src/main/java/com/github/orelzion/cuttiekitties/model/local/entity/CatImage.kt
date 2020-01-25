package com.github.orelzion.cuttiekitties.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CatImage(
    @PrimaryKey val id: String,
    val url: String,
    val breed_id: String
)