package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

    private var showError = false

    fun setError(value: Boolean) {
        showError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(showError) {
            return Result.Error("Reminders could not found")
        }

        reminders?.let { return Result.Success(ArrayList(it)) }

        return Result.Error("Reminders could not found")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = reminders?.firstOrNull { reminder ->
            reminder.id == id
        }

        if(reminder != null) {
            return Result.Success(reminder)
        }

        return Result.Error("Reminder could not found.")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }
}