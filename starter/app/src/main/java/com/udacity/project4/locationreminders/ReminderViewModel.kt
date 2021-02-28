package com.udacity.project4.locationreminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.udacity.project4.utils.FirebaseUserLiveData

class ReminderViewModel : ViewModel() {
    companion object {
        val androidFacts = arrayOf(
            "The first commercial Android device was launched in September 2008",
            "The Android operating system has over 2 billion monthly active users",
            "The first Android version (1.0) was released on September 23, 2008",
            "The first smart phone running Android was the HTC Dream called the T-Mobile G1 in " + "some countries"
        )

        val californiaFacts = arrayOf(
            "The most populated state in the United States is California",
            "Three out of the ten largest U. S. cities are in California",
            "The largest tree in the world can be found in California",
            "California became a state in 1850"
        )
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if(user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}
