package com.example.travelpoints

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.example.travelpoints.databinding.MainActivityLayoutBinding
import com.example.travelpoints.models.isCurrentUserAdmin
import com.example.travelpoints.ui.fragments.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage


class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityLayoutBinding

    private lateinit var loginFragment: LoginFragment
    private lateinit var mapFragment: MapFragment

    private lateinit var bottomBar: BottomNavigationView

    init {
        eventLiveData.observeForever{updateBottomBarVisibility()}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.Theme_TravelPoints)

        setupFragmentNavigation()
        setupFirebaseMessaging()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupFragmentNavigation() {
        bottomBar = binding.bottomNavigationView

        val accountFragment = AccountFragment(
            navigateToLoginFragment = {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginFragment).commit()
            }, navigateToSiteDetails = {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mapFragment).commit()
                bottomBar.selectedItemId = R.id.map
                mapFragment.siteToPositionAt = it
            }
        )

        mapFragment = MapFragment(
            navigateToSiteCreation = { lat, long ->
            val siteCreationFragment = SiteCreationFragment(
                lat = lat,
                long = long,
                navigateToMapFragment = {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mapFragment).commit()
                }
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, siteCreationFragment).commit()
        },
            navigateToSiteDetails = {
                val siteDetailsFragment = SiteDetailsFragment(
                    site = it,
                    navigateToMapFragment = {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, mapFragment).commit()
                    }
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, siteDetailsFragment).commit()
            })
        val supportFragment = SupportFragment()
        val registerFragment = RegisterFragment(
            navigateToAccountFragment = {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, accountFragment).commit()
            }, navigateToLoginFragment = {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginFragment).commit()
            })
        loginFragment = LoginFragment(
            navigateToRegisterFragment = {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, registerFragment).commit()
            },
            navigateToAccountFragment = {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, accountFragment).commit()
            }
        )
        val chartsFragment = ChartsFragment()

        bottomBar.selectedItemId = R.id.map
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mapFragment)
            .commit()

        updateBottomBarVisibility()

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account -> {
                    if (FirebaseAuth.getInstance().currentUser != null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, accountFragment)
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, loginFragment)
                            .commit()
                    }
                }

                R.id.map -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mapFragment)
                        .commit()
                }

                R.id.support -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, supportFragment)
                        .commit()
                }
                R.id.charts -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, chartsFragment)
                        .commit()
                }
            }
            true
        }
    }

    private fun updateBottomBarVisibility() {
        bottomBar.menu.findItem(R.id.support).isVisible = !isCurrentUserAdmin()
        bottomBar.menu.findItem(R.id.charts).isVisible = isCurrentUserAdmin()
    }

    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(object: OnCompleteListener<String>{
            override fun onComplete(task: Task<String>) {
                if (!task.isSuccessful) {
                    return
                }
                val token = task.result
            }
        })
    }

    fun generateNotification(siteName: String, oldPrice: String, newPrice: String, id: Int) {
        val title = "Discount on $siteName"
        val text = "From $oldPrice$ to $newPrice$"
        val CHANNEL_ID = "MESSAGE"
        val channel = NotificationChannel(CHANNEL_ID, "Message Notfication", NotificationManager.IMPORTANCE_DEFAULT)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: Notification.Builder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_chart)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(id, notification.build())
    }

    companion object {

        val eventLiveData = MutableLiveData<Unit>()

        fun sendEmail(recipients: List<String>, subject: String, body: String, context: Context) {

            val addresses: Array<String> = recipients.toTypedArray()

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, addresses)
                putExtra(Intent.EXTRA_SUBJECT,subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(context, Intent.createChooser(emailIntent, "Send Email"), null)
        }
    }
}