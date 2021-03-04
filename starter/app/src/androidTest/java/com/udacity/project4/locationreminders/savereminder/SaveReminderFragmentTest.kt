package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.local.FakeAndroidDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
@MediumTest
class SaveReminderFragmentTest : KoinTest {

    private lateinit var database: RemindersDatabase
    private val dataSource: FakeAndroidDataSource by inject()

    private val testModules = module {
        viewModel {
            SaveReminderViewModel(
                    get(),
                    get() as FakeAndroidDataSource
            )
        }
        single {
            database = Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    RemindersDatabase::class.java
            ).allowMainThreadQueries().build()
        }
        single { FakeAndroidDataSource() }
        single { database.reminderDao() }
    }

    @Before
    fun setupFragment() {
        stopKoin()
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext<Application>())
            modules(listOf(testModules))
        }
    }

    @After
    fun removeFragment() {
        stopKoin()
    }

    @Test
    fun saveReminderFragment_blankFragmentCheck() {
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.saveReminder)).check(matches(isDisplayed()))
    }

    @Test
    fun saveReminderFragment_RedirectionToMapFragment() {
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)

        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.selectLocation)).perform(click())

        verify(navController).navigate(
                SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment()
        )
    }
}