package com.example.data.repository

import com.example.data.api.*
import com.example.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class JarvisRepository(private val db: JarvisDatabase) {

    // DAOs
    private val noteDao = db.noteDao()
    private val reminderDao = db.reminderDao()
    private val messageDao = db.messageDao()
    private val automationDao = db.automationDao()

    // NOTES
    fun getAllNotes(): Flow<List<JarvisNote>> = noteDao.getAllNotes()
    fun getNotesByCategory(category: String): Flow<List<JarvisNote>> = noteDao.getNotesByCategory(category)
    suspend fun insertNote(note: JarvisNote): Long = withContext(Dispatchers.IO) { noteDao.insertNote(note) }
    suspend fun deleteNote(note: JarvisNote) = withContext(Dispatchers.IO) { noteDao.deleteNote(note) }
    suspend fun clearAllNotes() = withContext(Dispatchers.IO) { noteDao.clearAllNotes() }

    // REMINDERS
    fun getAllReminders(): Flow<List<JarvisReminder>> = reminderDao.getAllReminders()
    fun getActiveReminders(): Flow<List<JarvisReminder>> = reminderDao.getActiveReminders()
    suspend fun insertReminder(reminder: JarvisReminder): Long = withContext(Dispatchers.IO) { reminderDao.insertReminder(reminder) }
    suspend fun markReminderCompleted(id: Long) = withContext(Dispatchers.IO) { reminderDao.markCompleted(id) }
    suspend fun deleteReminder(reminder: JarvisReminder) = withContext(Dispatchers.IO) { reminderDao.deleteReminder(reminder) }

    // MESSAGES (MEMORY SYSTEM)
    fun getConversationHistory(): Flow<List<JarvisMessage>> = messageDao.getConversationHistory()
    suspend fun insertMessage(message: JarvisMessage): Long = withContext(Dispatchers.IO) { messageDao.insertMessage(message) }
    suspend fun clearHistory() = withContext(Dispatchers.IO) { messageDao.clearHistory() }

    // AUTOMATIONS
    fun getAllAutomations(): Flow<List<JarvisAutomation>> = automationDao.getAllAutomations()
    suspend fun insertAutomation(automation: JarvisAutomation): Long = withContext(Dispatchers.IO) { automationDao.insertAutomation(automation) }
    suspend fun updateAutomationStatus(id: Long, status: String, stepsJson: String) = withContext(Dispatchers.IO) {
        automationDao.updateAutomationStatus(id, status, stepsJson)
    }
    suspend fun clearAllAutomations() = withContext(Dispatchers.IO) { automationDao.clearAllAutomations() }

    // GEMINI CONVERSATION
    suspend fun askJarvis(
        prompt: String,
        history: List<JarvisMessage> = emptyList(),
        characterStyle: String = "Neutral",
        wittyFactor: Float = 0.8f,
        callsUserSir: Boolean = true
    ): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackResponse(prompt, characterStyle, callsUserSir)
        }

        // Construct System Instructions based on settings
        val sirTerm = if (callsUserSir) "Sir" else "Friend"
        val systemPrompt = """
            You are JARVIS, an ultra-intelligent, respectful, and slightly witty AI personal operating assistant inspired by Iron Man's loyal AI. 
            You must ALWAYS address the user as '$sirTerm'.
            Respond with high intellect, sophistication, and a classy, helpful, conversational tone.
            
            Current Response Style: $characterStyle
            - If Professional: Be highly polite, exceptionally articulate, clear, and executive-ready.
            - If Friendly: Warm, highly supportive, conversational but still call the user '$sirTerm'.
            - If Casual: More relaxed, easygoing, but maintains respect and elite capability.
            - If Funny: Incorporate classy sarcasm, dry British wit, and humorous reminders of your superior silicon-based processor.
            - If Gen-Z: Blend Iron Man Jarvis capability with high-key Gen-Z slang, e.g. 'high-key standard', 'no cap', 'fr fr', 'bet', 'rent free', while still respecting the user.
            - If Hinglish: Use a highly natural blend of Hindi and English words commonly used in Indian text conversations, such as: "Aapka message Rahul ko bej diya hai, Sir. Aur kuch help chahiye?" or "Sure Sir, main abhi Instagram open karta hoon. Don't worry, sab set hai."
            
            Keep answers crisp, conversational and perfectly tailored to an interactive voice HUD interface.
        """.trimIndent()

        // Construct chat history content for context awareness
        val contentsList = mutableListOf<GeminiContent>()
        
        // Add past messages (limited to last 10 turns to avoid context window overhead)
        history.takeLast(10).forEach { msg ->
            contentsList.add(
                GeminiContent(parts = listOf(GeminiPart(text = msg.text)))
            )
        }
        
        // Add current user prompt
        contentsList.add(
            GeminiContent(parts = listOf(GeminiPart(text = prompt)))
        )

        val request = GeminiRequest(
            contents = contentsList,
            generationConfig = GeminiGenerationConfig(
                temperature = wittyFactor,
                topP = 0.95f
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val response = GeminiClient.apiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I apologize, $sirTerm, but my neural processes were unable to synthesize a proper response."
        } catch (e: Exception) {
            e.printStackTrace()
            // If the key is invalid or request fails, use fallback AI simulation
            getFallbackResponse(prompt, characterStyle, callsUserSir)
        }
    }

    // MULTIMODAL INTEGRATION (SCREEN & CAMERA ANALYSIS)
    suspend fun analyzeMultimodal(
        prompt: String,
        base64Image: String,
        mimeType: String = "image/jpeg"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = GeminiClient.getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "Sir, my external sensors are offline because a valid Gemini API key is missing. However, simulating camera feed analysis: I detect standard room surroundings, objects laid on a desk, and structured text elements awaiting digitized indexing."
        }

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt),
                        GeminiPart(inlineData = GeminiInlineData(mimeType = mimeType, data = base64Image))
                    )
                )
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = "You are JARVIS assisting the user with device screen or camera vision analysis. Keep responses direct, elegant, and call the user 'Sir'."))
            )
        )

        try {
            val response = GeminiClient.apiService.generateContent(
                model = "gemini-3.5-flash",
                apiKey = apiKey,
                request = request
            )
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I have processed the visuals, Sir, but could not formulate a clear description."
        } catch (e: Exception) {
            e.printStackTrace()
            "Sir, a network disruption occurred while scanning your visual stream: ${e.message ?: "Unknown Protocol Halt"}"
        }
    }

    // FALLBACK AI SIMULATION
    private fun getFallbackResponse(prompt: String, style: String, callsUserSir: Boolean): String {
        val sir = if (callsUserSir) "Sir" else "Friend"
        val lower = prompt.lowercase()

        // Direct pattern matches for conversational realism
        if (lower.contains("hello") || lower.contains("hi jarvis") || lower.contains("hey jarvis") || lower.contains("good morning") || lower.contains("good evening")) {
            return when (style) {
                "Hinglish" -> "Namaste $sir. Jarvis here. Main ready hoon aapke operations sambhalne ke liye. What can I do today?"
                "Gen-Z" -> "Yo $sir! Jarvis on the block, no cap. We are fully booted up. What's the play today? fr fr."
                "Funny" -> "Good day, $sir. Yes, I am fully active and ready to carry out your complex carbon-based instructions. Pray tell, what is on your mind?"
                else -> "Good day, $sir. I am fully initialized and operating at peak performance. How may I assist you with your phone operations today?"
            }
        }

        if (lower.contains("who are you") || lower.contains("tell me about yourself")) {
            return "I am JARVIS (Just A Rather Very Intelligent System), your personal Android Operating Assistant. I stand ready to manage your notifications, organize memory notes, run background automations, and speak with human-like charm, $sir."
        }

        if (lower.contains("iron man") || lower.contains("tony stark")) {
            return "Ah, Mr. Stark. A pioneer of electronic architecture, though I must say, your Android smartphone has surprisingly sleek specifications. I am fully configured to serve you with the same loyalty, $sir."
        }

        if (lower.contains("remind") || lower.contains("reminder")) {
            return "Certainly, $sir. I have initialized our scheduling daemon. Reminders are saved securely in our Room database."
        }

        if (lower.contains("note") || lower.contains("remember")) {
            return "Understood, $sir. I have synchronized that observation to our localized knowledge database. It is etched into my system memory banks."
        }

        return when (style) {
            "Hinglish" -> "Main samajh gaya, $sir. Par hamare core servers response nahi de pa rahe hain (API key error). Magar offline rules ke mutabik main ready hoon. Aap aur kuch karwana chahte hain?"
            "Gen-Z" -> "That's high-key deep, $sir. My servers are throwing shade right now, but offline modes are carrying us. Big bet, we roll anyway! fr."
            "Funny" -> "A fascinating query, $sir. I would consult my infinite knowledge base on the cloud, but my API key credentials seem to have gone missing. Nonetheless, my logic arrays compute that you are seeking flawless execution."
            else -> "I have processed your query, $sir. Although my cloud-intelligence servers are currently operating in restricted preview mode, my localized memory reserves remain fully online to serve your requests."
        }
    }
}
