package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JarvisNoteDao {
    @Query("SELECT * FROM jarvis_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<JarvisNote>>

    @Query("SELECT * FROM jarvis_notes WHERE category = :category ORDER BY timestamp DESC")
    fun getNotesByCategory(category: String): Flow<List<JarvisNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: JarvisNote): Long

    @Delete
    suspend fun deleteNote(note: JarvisNote)

    @Query("DELETE FROM jarvis_notes")
    suspend fun clearAllNotes()
}

@Dao
interface JarvisReminderDao {
    @Query("SELECT * FROM jarvis_reminders ORDER BY dateTime ASC")
    fun getAllReminders(): Flow<List<JarvisReminder>>

    @Query("SELECT * FROM jarvis_reminders WHERE isCompleted = 0 ORDER BY dateTime ASC")
    fun getActiveReminders(): Flow<List<JarvisReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: JarvisReminder): Long

    @Query("UPDATE jarvis_reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Delete
    suspend fun deleteReminder(reminder: JarvisReminder)
}

@Dao
interface JarvisMessageDao {
    @Query("SELECT * FROM jarvis_messages ORDER BY timestamp ASC")
    fun getConversationHistory(): Flow<List<JarvisMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: JarvisMessage): Long

    @Query("DELETE FROM jarvis_messages")
    suspend fun clearHistory()
}

@Dao
interface JarvisAutomationDao {
    @Query("SELECT * FROM jarvis_automations ORDER BY timestamp DESC")
    fun getAllAutomations(): Flow<List<JarvisAutomation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomation(automation: JarvisAutomation): Long

    @Query("UPDATE jarvis_automations SET status = :status, stepsListJson = :stepsJson WHERE id = :id")
    suspend fun updateAutomationStatus(id: Long, status: String, stepsJson: String)

    @Query("DELETE FROM jarvis_automations")
    suspend fun clearAllAutomations()
}
