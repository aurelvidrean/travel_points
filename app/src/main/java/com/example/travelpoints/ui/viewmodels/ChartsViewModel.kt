package com.example.travelpoints.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChartsViewModel: ViewModel() {

    val lineChartEntries = MutableLiveData<MutableList<Entry>>(null)


    /**
     * Function to get total rating for each site
     */
    fun getLineChartEntries() {
        val newEntries = mutableListOf<Entry>()
        val ratingNumber = FirebaseDatabase.getInstance().getReference("Ratings")
        ratingNumber.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach { itRating ->
                        val rating = itRating.child("Rating").getValue(Double::class.java)
                        val siteId = itRating.child("SiteId").getValue(Double::class.java)
                        if (rating != null && siteId != null) {
                            newEntries.add(Entry(siteId.toFloat(), rating.toFloat()))
                        }
                    }
                    lineChartEntries.postValue(newEntries)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    val pieChartEntries = MutableLiveData<List<PieEntry>>(null)

    /**
     * Function to get the number sites of each category
     */
    fun getPieChartEntries() {
        val pieEntries = mutableListOf<PieEntry>()
        var museumEntries: Int = 0
        var parkEntries: Int = 0
        var monumentEntries: Int = 0
        val monumentCategory = FirebaseDatabase.getInstance().getReference("Sites")
        monumentCategory.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        when (it.child("Category").value) {
                            MONUMENT -> monumentEntries++
                            PARK -> parkEntries++
                            MUSEUM -> museumEntries++
                        }
                    }
                    pieEntries.add(PieEntry(museumEntries.toFloat(), MUSEUM))
                    pieEntries.add(PieEntry(parkEntries.toFloat(), PARK))
                    pieEntries.add(PieEntry(monumentEntries.toFloat(), MONUMENT))
                    pieChartEntries.postValue(pieEntries)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    val barChartEntries = MutableLiveData<List<BarEntry>>(null)

    /**
     * Function to get the number of comments for each site
     */
    fun getBarChartEntries() {
        val barEntries = mutableListOf<BarEntry>()
        val nbOfSites = hashMapOf<Int, Int>()
        val siteNumber = FirebaseDatabase.getInstance().getReference("Sites")
        siteNumber.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        val id = it.key!!.toInt()
                        nbOfSites[id] = 0
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val comments = FirebaseDatabase.getInstance().getReference("Comments")
        comments.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        val siteId = it.child("SiteId").getValue(Long::class.java)
                        if (siteId != null) {
                            if (nbOfSites.keys.contains(siteId.toInt())) {
                                val currentNbOfComments = nbOfSites[siteId.toInt()]
                                nbOfSites[siteId.toInt()] = currentNbOfComments!!.plus(1)
                            } else {
                                nbOfSites[siteId.toInt()] = 0
                            }
                        }
                    }
                }
                nbOfSites.keys.forEach {
                    if (nbOfSites[it]!! > 0) {
                        barEntries.add(BarEntry(it.toFloat(), nbOfSites[it]!!.toFloat()))
                    }
                }
                barChartEntries.postValue(barEntries)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {
        const val MONUMENT = "Monument"
        const val PARK = "Park"
        const val MUSEUM = "Museum"
    }
}