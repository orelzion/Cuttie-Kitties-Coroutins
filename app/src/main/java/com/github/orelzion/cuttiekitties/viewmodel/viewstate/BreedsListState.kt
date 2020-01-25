package com.github.orelzion.cuttiekitties.viewmodel.viewstate

import androidx.paging.PagedList
import com.github.orelzion.cuttiekitties.model.local.entity.Breed

sealed class BreedsListState {
    object Loading : BreedsListState()
    data class Error(val error: Throwable) : BreedsListState()
    data class Success(val breedsList: PagedList<Breed>) : BreedsListState()
}