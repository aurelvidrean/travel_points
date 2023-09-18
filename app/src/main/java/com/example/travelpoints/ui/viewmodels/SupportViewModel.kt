package com.example.travelpoints.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.travelpoints.MainActivity
import com.example.travelpoints.models.admins
import com.example.travelpoints.models.getActiveUserEmail

class SupportViewModel : ViewModel() {

    fun sendEmail(message: String, context: Context) {
        MainActivity.sendEmail(
            recipients = admins,
            subject = "Message support from " + getActiveUserEmail(),
            body = message,
            context = context
        )
    }
}