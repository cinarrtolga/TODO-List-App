package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding
    private val viewModel by viewModels<AuthenticationViewModel>()

    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)

        //Assigning click function for login button.
        binding.loginButton.setOnClickListener { startSignInFlow() }

        //Authentication control. When user authenticated, next activity starting.
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationViewModel.AuthenticationState.AUTHENTICATED -> {
                    startActivity(Intent(this, RemindersActivity::class.java))
                }
                else -> Log.i(
                        "warning",
                        "User is unauthenticated."
                )
            }
        })
    }

    //With this function providing sign-in flow. For this sample we just have e-mail and google.
    //It works with a custom layout xml which is coming from the layout folder. (custom_layout_xml)
    private fun startSignInFlow() {
        val providers: MutableList<IdpConfig> = ArrayList()
        providers.add(EmailBuilder().build())
        providers.add(GoogleBuilder().build())

        val customLayout = AuthMethodPickerLayout
                .Builder(R.layout.custom_login_layout)
                .setGoogleButtonId(R.id.login_google_button)
                .setEmailButtonId(R.id.login_email_button)
                .build()

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(customLayout)
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme)
                        .setIsSmartLockEnabled(false)
                        .build(),
                SIGN_IN_RESULT_CODE
        )
    }
}
