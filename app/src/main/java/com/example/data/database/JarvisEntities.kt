package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jarvis_notes")
data class JarvisNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // e.g., "Personal", "Work", "Knowledge"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "jarvis_reminders")
data class JarvisReminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val dateTime: Long, // Epoch timestamp for delivery
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "jarvis_messages")
data class JarvisMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "jarvis"
    val text: String,
    val style: String = "Normal", // "Professional", "Friendly", "Casual", "Funny", "Gen-Z", "Hinglish"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "jarvis_automations")
data class JarvisAutomation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val command: String,
    val status: String, // "Success", "Failed", "In Progress"
    val stepsListJson: String, // Serialized list of steps and their states
    val timestamp: Long = System.currentTimeMillis()
)
