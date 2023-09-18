package com.example.travelpoints.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelpoints.models.Site
import com.example.travelpoints.ui.theme.TravelPointsTheme
import com.example.travelpoints.ui.viewmodels.AccountViewModel
import com.example.travelpoints.ui.viewmodels.MapFragmentViewModel
import com.example.travelpoints.ui.views.AccountView
import com.google.android.gms.maps.model.LatLng

class AccountFragment(
    private val navigateToLoginFragment: () -> Unit,
    private val navigateToSiteDetails: (Site) -> Unit
) : Fragment() {

    private val accountViewModel by lazy {
        ViewModelProvider(this)[AccountViewModel::class.java]
    }

    private val mapViewModel by lazy {
        ViewModelProvider(this)[MapFragmentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TravelPointsTheme {
                    AccountView(
                        navigateToLoginFragment = navigateToLoginFragment,
                        viewModel = accountViewModel,
                        navigateToSiteDetails = navigateToSiteDetails
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapViewModel.getAllSites()
        mapViewModel.sites.observe(viewLifecycleOwner) { sites ->
            if (sites != null) {
                accountViewModel.updateSites(sites)
            }
        }
    }
}