package com.weather.forecast.clearsky.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.model.City

class SearchListAdapter(
    private val noResultTextView: TextView,
    private val listener: CustomOnClickListener,
) : RecyclerView.Adapter<SearchListAdapter.CustomViewHolder>() {
    private var filteredList: List<City> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_filter_item_view, parent, false)
        val holder = CustomViewHolder(view)
        holder.addButton.setOnClickListener {
            listener.onClick(filteredList[holder.adapterPosition], holder)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val curr = filteredList[position]
        holder.city.text = curr.city
        holder.country.text = curr.country
        if (curr.isTracked) {
            holder.statusText.visibility = View.VISIBLE
            holder.addButton.visibility = View.GONE
        } else {
            holder.statusText.visibility = View.GONE
            holder.addButton.visibility = View.VISIBLE
        }
    }

    fun updateFilteredList(newList: List<City>) {
        this.filteredList = newList
        if (filteredList.isEmpty()) {
            noResultTextView.visibility = View.VISIBLE
        } else {
            noResultTextView.visibility = View.GONE
        }
        notifyDataSetChanged()
    }


    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val city: TextView = itemView.findViewById(R.id.city_name)
        val country: TextView = itemView.findViewById(R.id.country_name)
        val addButton: ImageView = itemView.findViewById(R.id.add_btn)
        val statusText : TextView = itemView.findViewById(R.id.status_text)
    }
}

interface CustomOnClickListener {
    fun onClick(location: City, holder: SearchListAdapter.CustomViewHolder)
}
