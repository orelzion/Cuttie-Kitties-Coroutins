package com.github.orelzion.cuttiekitties.viewmodel

import androidx.lifecycle.*
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.repository.CatRepository
import com.github.orelzion.cuttiekitties.viewmodel.viewstate.BreedsListState
import kotlinx.coroutines.launch

class BreedsListViewModel(private val repository: CatRepository) : ViewModel() {

    class BreedsListModelFactory(private val repository: CatRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BreedsListViewModel(repository) as T
        }
    }

    private val _stateObserver = MediatorLiveData<BreedsListState>()
    val stateObserver: LiveData<BreedsListState> = _stateObserver

    fun viewDidLoad() {
        _stateObserver.addSource(repository.getBreedListLiveData(viewModelScope)) {
            if (it.isEmpty()) {
                refresh()
            } else {
                _stateObserver.value = BreedsListState.Success(it)
            }
        }
    }

    fun refresh() {
        _stateObserver.value = BreedsListState.Loading
        viewModelScope.launch {
            try {
                repository.refreshBreeds()
            } catch (error: Throwable) {
                _stateObserver.postValue(BreedsListState.Error(error))
            }
        }
    }

    fun onBreedItemClicked(breed: Breed) {

    }
}