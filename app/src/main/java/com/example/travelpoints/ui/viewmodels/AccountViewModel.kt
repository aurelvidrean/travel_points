package com.example.travelpoints.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.travelpoints.models.Site
import com.example.travelpoints.models.getActiveUserId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountViewModel: ViewModel() {

    private val _wishlistSites:MutableStateFlow<List<Site>> = MutableStateFlow(listOf())
    val wishlistSites = _wishlistSites.asStateFlow()

    fun updateSites(allSites: List<Site>) {
        val firebaseReference = FirebaseDatabase.getInstance().getReference("Wishlist").child(getActiveUserId().toString())
        firebaseReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _wishlistSites.value = listOf()
                    snapshot.children.forEach { siteSnapshot ->
                        val siteIdString = siteSnapshot.key
                        allSites.forEach { site ->
                            if (site.id.toString() == siteIdString && siteSnapshot.getValue(Boolean::class.java) == true) {
                                _wishlistSites.value += site
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}