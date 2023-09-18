package com.example.travelpoints.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.travelpoints.MainActivity
import com.example.travelpoints.R
import com.example.travelpoints.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment(
    private val navigateToAccountFragment: () -> Unit,
    private val navigateToLoginFragment: () -> Unit)
    : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        email = view.findViewById(R.id.email_input)
        password = view.findViewById(R.id.password_input)
        registerButton = view.findViewById(R.id.register_button)
        loginButton = view.findViewById(R.id.already_have_account_textview)
        progressBar = view.findViewById(R.id.progress_bar)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            navigateToLoginFragment()
        }

        loginButton.setOnClickListener {
            navigateToLoginFragment()
        }

        registerButton.setOnClickListener {
            val emailString = email.text.toString().trim()
            val passwordString = password.text.toString().trim()

            if (TextUtils.isEmpty(emailString)) {
                email.error = "Email is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(passwordString)) {
                password.error = "Password is required"
                return@setOnClickListener
            }

            if (passwordString.length < 4) {
                password.error = "Password needs to be at least 5 characters long"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Account created", Toast.LENGTH_SHORT).show()
                    Log.d("success", "blabla")
                    navigateToAccountFragment()
                    MainActivity.eventLiveData.postValue(Unit)
                } else {
                    Log.d("fail", "blabla")
                    Toast.makeText(requireContext(), "Account not created ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }
}