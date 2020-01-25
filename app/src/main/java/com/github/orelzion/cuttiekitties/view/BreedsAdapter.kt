package com.github.orelzion.cuttiekitties.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.orelzion.cuttiekitties.R
import com.github.orelzion.cuttiekitties.model.local.entity.Breed

class BreedsAdapter(private val onItemClickListener: (breed: Breed) -> Unit) :
    PagedListAdapter<Breed, BreedsAdapter.BreedsViewHolder>(DIFF_CALLBACK) {

    class BreedsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup): BreedsViewHolder {
                return BreedsViewHolder(inflater.inflate(R.layout.view_holder_breed, parent, false))
            }
        }

        fun setText(breedName: String) {
            (itemView as TextView).apply {
                text = breedName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedsViewHolder {
        return BreedsViewHolder
            .create(LayoutInflater.from(parent.context), parent)
            .also { breedsViewHolder ->
                breedsViewHolder.itemView.setOnClickListener {
                    getItem(breedsViewHolder.adapterPosition)?.let {
                        onItemClickListener.invoke(it)
                    }
                }
            }
    }

    override fun onBindViewHolder(holder: BreedsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.setText(it.name)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Breed>() {
            override fun areItemsTheSame(oldItem: Breed, newItem: Breed) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Breed, newItem: Breed) = oldItem == newItem
        }
    }
}
