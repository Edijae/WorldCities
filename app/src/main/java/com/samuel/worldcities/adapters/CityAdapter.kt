package com.samuel.worldcities.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.samuel.worldcities.databinding.CityItemBinding
import com.samuel.worldcities.models.City


class CityAdapter
    : PagingDataAdapter<City, CityViewHolder>(Comparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(
            CityItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.setCity(getItem(position))
    }

}

class CityViewHolder(val binding: CityItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun setCity(city: City?) {
        binding.city = city
    }
}

class Comparator : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }
}

