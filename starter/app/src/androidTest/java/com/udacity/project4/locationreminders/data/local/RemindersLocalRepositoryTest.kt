package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun repositorySetup() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        remindersLocalRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun repositoryClean() {
        database.close()
    }

    @Test
    fun repository_saveAndGetCheck() = runBlocking {
        val newReminder = ReminderDTO("Hello", "Hello World!", "World", 55.05, -05.55)
        remindersLocalRepository.saveReminder(newReminder)

        val result = remindersLocalRepository.getReminder(newReminder.id)
        result as Result.Success

        assertThat(result.data.id, `is`(newReminder.id))
        assertThat(result.data.title, `is`(newReminder.title))
        assertThat(result.data.description, `is`(newReminder.description))
        assertThat(result.data.location, `is`(newReminder.location))
        assertThat(result.data.latitude, `is`(newReminder.latitude))
        assertThat(result.data.longitude, `is`(newReminder.longitude))
    }

    @Test
    fun repository_getReminderListCheck() = runBlocking {
        val reminder = ReminderDTO("Hello", "Hello World!", "World", 55.05, -05.55)
        remindersLocalRepository.saveReminder(reminder)
        val reminderList = remindersLocalRepository.getReminders()
        reminderList as Result.Success
        assertThat(reminderList.data.count(), `is`(1))
    }

    @Test
    fun repository_clearReminderListCheck() = runBlocking {
        val reminder = ReminderDTO("Hello", "Hello World!", "World", 55.05, -05.55)
        remindersLocalRepository.saveReminder(reminder)
        val reminderList = remindersLocalRepository.getReminders()
        remindersLocalRepository.deleteAllReminders()
        val updatedReminderList = remindersLocalRepository.getReminders()

        reminderList as Result.Success
        updatedReminderList as Result.Success

        assertThat(reminderList.data.count(), `is`(1))
        assertThat(updatedReminderList.data.count(), `is`(0))
    }
}