package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
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
class SaveReminderViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var reminderDataSource: FakeDataSource
    private lateinit var application: Application

    @Before
    fun setupSaveReminderViewModel() {
        application = ApplicationProvider.getApplicationContext<Application>()
        reminderDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(application, reminderDataSource)
    }

    @After
    fun removeSaveReminderViewModel() {
        stopKoin()
    }

    @Test
    fun saveReminder_toastValueCheck() {
        val reminder = ReminderDataItem("Hello", "Test", "Sample", 24.05, 35.05, "TestSampleId01")
        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showToast.value!!, `is`("Reminder Saved !"))
    }

    @Test
    fun saveReminder_titleValidationCheck() {
        val reminder = ReminderDataItem("", "Test", "Sample", 24.05, 35.05, "TestSampleId01")
        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showSnackBarInt.value!!, `is`(R.string.err_enter_title))
    }

    @Test
    fun saveReminder_locationValidationCheck(){
        val reminder = ReminderDataItem("Hello", "Test", "", 24.05, 35.05, "TestSampleId01")
        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showSnackBarInt.value!!, `is`(R.string.err_select_location))
    }
}