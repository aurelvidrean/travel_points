package com.example.travelpoints.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.travelpoints.ui.theme.TravelPointsTheme
import com.example.travelpoints.ui.views.SiteCreationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.values

class SiteCreationFragment(
    private val lat: Double,
    private val long: Double,
    private val navigateToMapFragment: () -> Unit
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TravelPointsTheme {
                    SiteCreationView(
                        lat = lat,
                        long = long,
                        onScreenClose = {
                            navigateToMapFragment()
                        },
                        onSaveSite = { name, description, entryPrice, category ->
                            saveSiteToFirebase(lat, long, name, description, entryPrice, category)
                        }
                    )
                }
            }
        }
    }

    private fun saveSiteToFirebase (lat: Double, long: Double, name: String, description: String, entryPrice: Double, category: String){
        val siteNumber = FirebaseDatabase.getInstance().getReference("SiteNumber").child("ID")
        siteNumber.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val currentId = (snapshot.value as Long) + 1
                    val firebaseReference = FirebaseDatabase.getInstance().getReference("Sites").child("$currentId")

                    firebaseReference.child("ID").setValue(currentId)
                    firebaseReference.child("Location").child("Latitude").setValue(lat)
                    firebaseReference.child("Location").child("Longitude").setValue(long)
                    firebaseReference.child("Name").setValue(name)
                    firebaseReference.child("Description").setValue(description)
                    firebaseReference.child("EntryPrice").setValue(entryPrice)
                    firebaseReference.child("OfferValue").setValue(0.0)
                    firebaseReference.child("Category").setValue(category)

                    siteNumber.setValue(currentId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}