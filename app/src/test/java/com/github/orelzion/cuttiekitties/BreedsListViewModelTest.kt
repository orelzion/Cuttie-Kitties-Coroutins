package com.github.orelzion.cuttiekitties

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.toLiveData
import com.github.orelzion.cuttiekitties.model.local.CatImagesDao
import com.github.orelzion.cuttiekitties.model.local.entity.Breed
import com.github.orelzion.cuttiekitties.model.network.CatApi
import com.github.orelzion.cuttiekitties.model.repository.BreedsBoundaryCallback
import com.github.orelzion.cuttiekitties.model.repository.CatRepository
import com.github.orelzion.cuttiekitties.viewmodel.BreedsListViewModel
import com.github.orelzion.cuttiekitties.viewmodel.viewstate.BreedsListState
import com.jraska.livedata.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BreedsListViewModelTest {

    private lateinit var viewModel: BreedsListViewModel
    private lateinit var repository: CatRepository
    private lateinit var catImagesDao: CatImagesDao
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun loadViewModel() {
        Dispatchers.setMain(mainThreadSurrogate)
        catImagesDao = CatDaoFake()
        repository = CatRepository(CatApiFake(), catImagesDao)
        viewModel = BreedsListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `testing view did load, expected to change state to loading`() = runBlockingTest {
        // Given, live data is empty

        // When
        viewModel.viewDidLoad()

        // Then state is loading
        viewModel.stateObserver
            .test()
            .assertHasValue()
            .assertValue(BreedsListState.Loading)
    }

    @Test
    fun `testing view did load, expected to load data from DB successfully`() = runBlockingTest {
        // Given, DB have breeds
        val breeds = listOf<Breed>(mockk())
        catImagesDao.insertBreeds(breeds)

        val allBreeds = catImagesDao.getAllBreeds().toLiveData(
            pageSize = CatApi.PAGE_SIZE,
            boundaryCallback = BreedsBoundaryCallback(this, repository = repository)
        )

        // When
        viewModel.viewDidLoad()

        // Then, state should be success with data
        viewModel.stateObserver
            .test()
            .assertHasValue()
            .assertValue(BreedsListState.Success(allBreeds.test().value()))
    }

    @Test
    fun `testing view did load, expected load data to fail and state to be changed to error`() = runBlockingTest {
        // Given
        val catApi = mockk<CatApi>()
        repository = CatRepository(catApi, catImagesDao)
        viewModel = BreedsListViewModel(repository)

        val exception = Exception("exception")
        coEvery { catApi.fetchBreedsList(any(), any()) } throws exception

        // When
        viewModel.viewDidLoad()

        // Then
        viewModel.stateObserver
            .test()
            .awaitNextValue()
            .assertHasValue()
            .assertValue(BreedsListState.Error(exception))
    }
}