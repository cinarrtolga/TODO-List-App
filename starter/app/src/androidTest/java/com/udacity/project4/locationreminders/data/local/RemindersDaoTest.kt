package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun database_saveAndGetCheck() = runBlocking {
        val reminder = ReminderDTO("Hello", "Hello World!", "World", 55.05, -05.55)
        database.reminderDao().saveReminder(reminder)

        val savedReminder = database.reminderDao().getReminderById(reminder.id)

        assertThat<ReminderDTO>(savedReminder as ReminderDTO, notNullValue())
        assertThat(savedReminder.id, `is`(reminder.id))
        assertThat(savedReminder.title, `is`(reminder.title))
        assertThat(savedReminder.description, `is`(reminder.description))
        assertThat(savedReminder.location, `is`(reminder.location))
        assertThat(savedReminder.latitude, `is`(reminder.latitude))
        assertThat(savedReminder.longitude, `is`(reminder.longitude))
    }

    @Test
    fun database_getReminderListCheck() = runBlocking {
        val reminder = ReminderDTO("Hello", "Hello World!", "World", 55.05, -05.55)
        database.reminderDao().saveReminder(reminder)

        val reminderList = database.reminderDao().getReminders()

        assertThat(reminderList.count(), `is`(1))
    }

    @Test
    fun database_clearReminderListCheck() = runBlocking {
        val reminder = ReminderDTO("Hello", "Hello World!", "World", 55.05, -05.55)
        database.reminderDao().saveReminder(reminder)
        val reminderList = database.reminderDao().getReminders()
        database.reminderDao().deleteAllReminders()
        val updatedReminderList = database.reminderDao().getReminders()

        assertThat(reminderList.count(), `is`(1))
        assertThat(updatedReminderList.count(), `is`(0))
    }
}