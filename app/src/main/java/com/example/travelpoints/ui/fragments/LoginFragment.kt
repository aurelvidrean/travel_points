package com.example.travelpoints.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.travelpoints.MainActivity
import com.example.travelpoints.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment(
    private val navigateToRegisterFragment: () -> Unit,
    private val navigateToAccountFragment: () -> Unit
) : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = binding.emailInputLogin
        password = binding.passwordInputLogin
        loginButton = binding.loginButton
        createAccountButton = binding.createAccount
        progressBar = binding.progressBarLogin

        firebaseAuth = FirebaseAuth.getInstance()

        createAccountButton.setOnClickListener {
            navigateToRegisterFragment()
        }

        loginButton.setOnClickListener {
            val emailField = email.text.toString().trim()
            val passwordField = password.text.toString().trim()

            if (TextUtils.isEmpty(emailField)) {
                email.error = "Email is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(passwordField)) {
                password.error = "Password is required"
                return@setOnClickListener
            }

            if (passwordField.length < 5) {
                password.error = "Password needs to be at least 6 characters long"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            //authenticate the user

            firebaseAuth.signInWithEmailAndPassword(emailField, passwordField).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireActivity(), "Successfully logged in", Toast.LENGTH_SHORT).show()
                    navigateToAccountFragment()
                    MainActivity.eventLiveData.postValue(Unit)
                } else {
                    Toast.makeText(requireActivity(), "Error ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
        //inflater.inflate(R.layout.fragment_login, container, false)
    }
}