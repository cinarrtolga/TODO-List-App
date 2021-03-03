package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersListViewModel: RemindersListViewModel
    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var application: Application

    @Before
    fun setupRemindersViewModel() {
        application = ApplicationProvider.getApplicationContext<Application>()
        reminderDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(application, reminderDataSource)
    }

    @After
    fun removeViewModel() {
        stopKoin()
    }

    @Test
    fun loadReminders_loadingFlowCheck() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_noDataResultCheck() {
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_saveReminderCheck() = runBlocking {
        val reminder = ReminderDTO("Hello", "Test", "Saample", 24.05, 35.05, "TestSampleId01")
        reminderDataSource.saveReminder(reminder)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.value!!.size, `is`(1))
        assertThat(remindersListViewModel.remindersList.value!!.size, `is`(not(2)))
    }

    @Test
    fun loadReminders_showErrorMessageCheck() {
        reminderDataSource.setError(true)
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showSnackBar.value, `is`("Reminders could not found"))
    }
}