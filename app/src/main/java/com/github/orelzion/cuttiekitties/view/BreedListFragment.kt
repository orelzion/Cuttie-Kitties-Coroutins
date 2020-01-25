package com.github.orelzion.cuttiekitties.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.orelzion.cuttiekitties.R
import com.github.orelzion.cuttiekitties.model.local.getDatabase
import com.github.orelzion.cuttiekitties.model.network.catApi
import com.github.orelzion.cuttiekitties.model.repository.CatRepository
import com.github.orelzion.cuttiekitties.viewmodel.BreedsListViewModel
import com.github.orelzion.cuttiekitties.viewmodel.viewstate.BreedsListState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_breed_list.*
import timber.log.Timber

class BreedListFragment : Fragment() {

    private lateinit var breedsListViewModel: BreedsListViewModel
    private lateinit var breedsAdapter: BreedsAdapter
    private val catRepository by lazy { CatRepository(catApi, getDatabase(context!!).catImagesDao) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create view model and pass it with the repository
        breedsListViewModel = ViewModelProviders.of(
            this,
            BreedsListViewModel.BreedsListModelFactory(catRepository)
        )[BreedsListViewModel::class.java]

        // Observe state changes
        breedsListViewModel.stateObserver.observe(this, Observer { onBreedsListStateChanged(it) })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_breed_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        breedsListViewModel.viewDidLoad()

        breedsAdapter = BreedsAdapter(breedsListViewModel::onBreedItemClicked)
        breedsListView.adapter = breedsAdapter
        breedsListView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun onBreedsListStateChanged(state: BreedsListState) {
        Timber.i("breed list state has changed to %s", state)
        progressLayout.visibility =
            if (state is BreedsListState.Loading) View.VISIBLE else View.GONE
        when (state) {
            is BreedsListState.Error -> handleError(state.error)
            is BreedsListState.Success -> breedsAdapter.submitList(state.breedsList)
        }
    }

    private fun handleError(error: Throwable) {
        Snackbar.make(breedsLayout, error.localizedMessage, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.try_again) { breedsListViewModel.refresh() }
            .show()
    }
}