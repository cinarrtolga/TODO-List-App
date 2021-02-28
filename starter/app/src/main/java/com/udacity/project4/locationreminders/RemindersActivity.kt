package com.udacity.project4.locationreminders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.databinding.ActivityRemindersBinding
import kotlinx.android.synthetic.main.activity_reminders.*

/**
 * The RemindersActivity that holds the reminders fragments
 */
class RemindersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemindersBinding

    private val viewModel by viewModels<ReminderViewModel>()

    //Logout control in activity. If a user is unauthenticated, It is not possible to use any feature. 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reminders)

        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                ReminderViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                }
                else -> Log.e(
                    "warning",
                    "Authentication state that doesn't require any UI change $authenticationState"
                )
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (nav_host_fragment as NavHostFragment).navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
