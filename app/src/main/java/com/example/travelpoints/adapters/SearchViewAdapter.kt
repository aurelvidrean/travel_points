package com.example.travelpoints.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelpoints.R
import com.google.android.gms.maps.model.LatLng


class SearchViewAdapter : RecyclerView.Adapter<SearchViewHolder>() {
    var dataList: List<Pair<String, LatLng>> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onSiteClicked: ((LatLng) -> (Unit))? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.search_item_view, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val data: Pair<String,LatLng> = dataList[position]
        holder.bind(data)
        Log.d("VALEUVALEU",data.first)
        holder.onItemClicked = onSiteClicked
    }
}

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.searchItemTv)
        var onItemClicked: ((LatLng) -> (Unit))? = null

        fun bind(data: Pair<String,LatLng>) {
            titleTextView.text = data.first
            titleTextView.setOnClickListener{
                onItemClicked?.invoke(data.second)
            }
        }
    }