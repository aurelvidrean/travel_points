package com.example.travelpoints.models

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.travelpoints.util.drawableToBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.auth.FirebaseAuth

data class Site(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val entryPrice: Double,
    val description: String,
    val category: Category,
    var offerValue: Double
) {
    companion object {
        var allSites: List<Site>? = null
    }
}


enum class Category {
    Park,
    Museum,
    Monument
}

fun fromStringToCategory(string: String): Category {
    return when (string) {
        "Park" -> Category.Park
        "Museum" -> Category.Museum
        "Monument" -> Category.Monument
        else -> Category.Monument
    }
}

fun getActiveUserId(): String? =
    FirebaseAuth.getInstance().currentUser?.uid

fun isCurrentUserAdmin(): Boolean =
    admins.contains(
        FirebaseAuth.getInstance().currentUser?.email
    )

val admins = listOf("timaruflorin14@gmail.com", "alexbarladeanu@gmail.com", "aurelvidrean16@gmail.com", "Denisaiza@yahoo.com", "laurairimia192@gmail.com")

fun getActiveUserEmail(): String =
    FirebaseAuth.getInstance().currentUser?.email.toString()

fun getMarkerIcon(category: Category, context: Context): BitmapDescriptor {
    val identifier = when (category) {
        Category.Park -> "ic_park"
        Category.Museum -> "ic_museum"
        Category.Monument -> "ic_monument"
    }
    val drawable: Drawable = context.resources.getDrawable(
        context.resources.getIdentifier(
            identifier,
            "drawable",
            context.packageName
        )
    )
    return BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable)!!)
}