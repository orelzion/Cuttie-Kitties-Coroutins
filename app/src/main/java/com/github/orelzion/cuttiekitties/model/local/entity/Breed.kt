package com.github.orelzion.cuttiekitties.model.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Breed(
    val name: String,
    val alt_names: String? = null,
    @PrimaryKey val id: String
)