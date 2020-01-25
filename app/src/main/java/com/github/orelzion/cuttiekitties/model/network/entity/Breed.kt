package com.github.orelzion.cuttiekitties.model.network.entity

import kotlinx.serialization.Serializable

@Serializable
data class Breed(
    val name: String,
    val alt_names: String? = null,
    val id: String
)

typealias BreedResponse = List<Breed>