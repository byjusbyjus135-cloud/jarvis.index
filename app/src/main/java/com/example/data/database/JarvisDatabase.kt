package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        JarvisNote::class,
        JarvisReminder::class,
        JarvisMessage::class,
        JarvisAutomation::class
    ],
    version = 1,
    exportSchema = false
)
abstract class JarvisDatabase : RoomDatabase() {

    abstract fun noteDao(): JarvisNoteDao
    abstract fun reminderDao(): JarvisReminderDao
    abstract fun messageDao(): JarvisMessageDao
    abstract fun automationDao(): JarvisAutomationDao

    companion object {
        @Volatile
        private var INSTANCE: JarvisDatabase? = null

        fun getDatabase(context: Context): JarvisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JarvisDatabase::class.java,
                    "jarvis_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
