package com.github.orelzion.cuttiekitties.model.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class CatImage(
    val id: String,
    val url: String,
    val breeds: List<Breed>
)

typealias CatImageResponse = List<CatImage>