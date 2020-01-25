package com.github.orelzion.cuttiekitties.model.repository

import androidx.paging.PagedList
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BreedsBoundaryCallback(
    private val coroutineScope: CoroutineScope,
    private val repository: CatRepository
) : PagedList.BoundaryCallback<Breed>() {

    private var lastItemAtEnd: Breed? = null

    override fun onZeroItemsLoaded() {
        super.onZeroItemsLoaded()
        coroutineScope.launch {
            repository.refreshBreeds()
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: Breed) {
        super.onItemAtEndLoaded(itemAtEnd)
        if (itemAtEnd != lastItemAtEnd) {
            lastItemAtEnd = itemAtEnd
            coroutineScope.launch {
                repository.refreshBreeds()
            }
        }
    }
}