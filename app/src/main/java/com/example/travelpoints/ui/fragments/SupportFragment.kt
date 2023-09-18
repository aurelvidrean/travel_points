package com.example.travelpoints.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelpoints.ui.theme.TravelPointsTheme
import com.example.travelpoints.ui.viewmodels.MapFragmentViewModel
import com.example.travelpoints.ui.viewmodels.SupportViewModel
import com.example.travelpoints.ui.views.SupportView

class SupportFragment: Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[SupportViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TravelPointsTheme {
                    SupportView(
                        onSend = { message, context ->
                            viewModel.sendEmail(message, context)
                        }
                    )
                }
            }
        }
    }
}