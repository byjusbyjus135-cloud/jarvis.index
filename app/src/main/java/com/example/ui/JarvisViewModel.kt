package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.JarvisRepository
import com.example.ui.voice.JarvisVoiceEngine
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

// Representation of simulated automation process step
data class AutomationStep(
    val title: String,
    val description: String,
    val status: String // "Pending", "Running", "Success", "Failed"
)

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getDatabase(application)
    val repository = JarvisRepository(db)
    val voiceEngine = JarvisVoiceEngine(application)

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val stepsAdapter = moshi.adapter<List<AutomationStep>>(
        Types.newParameterizedType(List::class.java, AutomationStep::class.java)
    )

    // UI Navigation State
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen

    // Conversational & System States
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _wakeWord = MutableStateFlow("Jarvis")
    val wakeWord: StateFlow<String> = _wakeWord

    private val _callsUserSir = MutableStateFlow(true)
    val callsUserSir: StateFlow<Boolean> = _callsUserSir

    private val _characterStyle = MutableStateFlow("Professional") // "Professional", "Friendly", "Casual", "Funny", "Gen-Z", "Hinglish"
    val characterStyle: StateFlow<String> = _characterStyle

    private val _wittyFactor = MutableStateFlow(0.8f)
    val wittyFactor: StateFlow<Float> = _wittyFactor

    // Speech-to-text / User Input text
    private val _userInputText = MutableStateFlow("")
    val userInputText: StateFlow<String> = _userInputText

    // Voice Engine active speaker
    val isSpeaking = voiceEngine.isSpeaking
    val selectedVoice = voiceEngine.currentVoice

    // Local DB Observers
    val allNotes: StateFlow<List<JarvisNote>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeReminders: StateFlow<List<JarvisReminder>> = repository.getActiveReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messageHistory: StateFlow<List<JarvisMessage>> = repository.getConversationHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAutomations: StateFlow<List<JarvisAutomation>> = repository.getAllAutomations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Automation simulated view
    private val _activeAutomationSteps = MutableStateFlow<List<AutomationStep>>(emptyList())
    val activeAutomationSteps: StateFlow<List<AutomationStep>> = _activeAutomationSteps

    private val _isAutomationRunning = MutableStateFlow(false)
    val isAutomationRunning: StateFlow<Boolean> = _isAutomationRunning

    private val _automationProgressLog = MutableStateFlow<List<String>>(emptyList())
    val automationProgressLog: StateFlow<List<String>> = _automationProgressLog

    // Camera Vision simulator state
    private val _cameraStatusText = MutableStateFlow("Sensors ready. Focus a physical target and command scan.")
    val cameraStatusText: StateFlow<String> = _cameraStatusText

    private val _selectedPresetImageIndex = MutableStateFlow(0)
    val selectedPresetImageIndex: StateFlow<Int> = _selectedPresetImageIndex

    private val _isCameraProcessing = MutableStateFlow(false)
    val isCameraProcessing: StateFlow<Boolean> = _isCameraProcessing

    // Screen Analysis simulator state
    private val _screenStatusText = MutableStateFlow("Navigate overlay or pick an active app mock-screen.")
    val screenStatusText: StateFlow<String> = _screenStatusText

    private val _selectedPresetScreenIndex = MutableStateFlow(0)
    val selectedPresetScreenIndex: StateFlow<Int> = _selectedPresetScreenIndex

    private val _isScreenProcessing = MutableStateFlow(false)
    val isScreenProcessing: StateFlow<Boolean> = _isScreenProcessing

    init {
        // Welcome Greeting
        speakIntro()
        // Save initial system prompts to memory
        seedDefaultConversationIfNeeded()
    }

    private fun seedDefaultConversationIfNeeded() {
        viewModelScope.launch {
            repository.getConversationHistory().first().let { history ->
                if (history.isEmpty()) {
                    repository.insertMessage(
                        JarvisMessage(sender = "jarvis", text = "Good day, Sir. All Jarvis physical modules and accessibility interfaces are online. Operating at peak computational efficiency.", style = "Professional")
                    )
                }
            }
        }
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun selectPresetImage(index: Int) {
        _selectedPresetImageIndex.value = index
    }

    fun selectPresetScreen(index: Int) {
        _selectedPresetScreenIndex.value = index
    }

    fun selectVoice(name: String) {
        voiceEngine.selectVoice(name)
        speak("Voice protocol updated, Sir.")
    }

    fun setWakeWord(word: String) {
        _wakeWord.value = word
    }

    fun setCallsUserSir(value: Boolean) {
        _callsUserSir.value = value
    }

    fun setCharacterStyle(style: String) {
        _characterStyle.value = style
        speak("Behavior algorithms configured to $style, Sir.")
    }

    fun setWittyFactor(factor: Float) {
        _wittyFactor.value = factor
    }

    fun updateUserInput(text: String) {
        _userInputText.value = text
    }

    fun startVoiceListening() {
        if (_isListening.value) {
            _isListening.value = false
            return
        }
        _isListening.value = true
        voiceEngine.stop()
        // Simulate a delay of voice detection then process the verbal command
        viewModelScope.launch {
            delay(3500)
            if (_isListening.value) {
                _isListening.value = false
                val promptText = _userInputText.value.ifBlank { "Jarvis, summarize our status." }
                processUserCommand(promptText)
                _userInputText.value = ""
            }
        }
    }

    fun processUserCommand(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            // Save user message to database
            repository.insertMessage(JarvisMessage(sender = "user", text = text, style = _characterStyle.value))

            val lowercase = text.lowercase()

            // 1. Core local notes/reminders command interceptors
            if (lowercase.startsWith("create a note") || lowercase.startsWith("create note") || lowercase.startsWith("remember that") || lowercase.startsWith("jarvis, remember")) {
                val content = text.replace("create a note", "", true)
                    .replace("create note", "", true)
                    .replace("remember that", "", true)
                    .replace("jarvis, remember", "", true)
                    .trim()
                
                val title = if (content.length > 20) content.substring(0, 18) + "..." else content.ifBlank { "AI Thought" }
                val noteId = repository.insertNote(
                    JarvisNote(title = title, content = content.ifBlank { "Recall requested note" }, category = "Knowledge")
                )
                val respText = "Certainly, Sir. I have recorded that under note entry #$noteId in our Room database."
                repository.insertMessage(JarvisMessage(sender = "jarvis", text = respText, style = _characterStyle.value))
                speak(respText)
                return@launch
            }

            if (lowercase.startsWith("remind me to") || lowercase.contains("remind me and set reminder")) {
                val content = text.replace("remind me to", "", true)
                    .replace("remind me", "", true)
                    .trim()
                
                // Set reminders 24 hours into the future as mock
                val reminderTime = System.currentTimeMillis() + (1000 * 60 * 60 * 24)
                val remId = repository.insertReminder(
                    JarvisReminder(text = content.ifBlank { "Re-align thrusters Core" }, dateTime = reminderTime)
                )
                val f = SimpleDateFormat("h:mm a, MMM dd", Locale.getDefault())
                val respText = "Understood. I have initialized a timer alert for '$content' scheduled on ${f.format(Date(reminderTime))}, Sir."
                repository.insertMessage(JarvisMessage(sender = "jarvis", text = respText, style = _characterStyle.value))
                speak(respText)
                return@launch
            }

            // 2. Automated routing or app operation commands detection
            if (lowercase.contains("instagram") && (lowercase.contains("caption") || lowercase.contains("open"))) {
                runInstagramAutomation()
                return@launch
            }
            if (lowercase.contains("play") && lowercase.contains("music") || lowercase.contains("spotify")) {
                runSpotifyAutomation()
                return@launch
            }
            if (lowercase.contains("message") || lowercase.contains("whatsapp") || lowercase.contains("rahul")) {
                runWhatsAppAutomation()
                return@launch
            }

            // Default: Ask Gemini API with full historical dialog structure for maximum context
            val history = messageHistory.value
            val jarvisResponseText = repository.askJarvis(
                prompt = text,
                history = history,
                characterStyle = _characterStyle.value,
                wittyFactor = _wittyFactor.value,
                callsUserSir = _callsUserSir.value
            )

            // Save response, trigger voice output
            repository.insertMessage(JarvisMessage(sender = "jarvis", text = jarvisResponseText, style = _characterStyle.value))
            speak(jarvisResponseText)
        }
    }

    fun deleteNote(note: JarvisNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
            speak("Deleted note, Sir.")
        }
    }

    fun completeReminder(reminder: JarvisReminder) {
        viewModelScope.launch {
            repository.markReminderCompleted(reminder.id)
            speak("Status marked as finalized in database, Sir.")
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
            speak("Secure terminal dialog memory purged, Sir.")
            seedDefaultConversationIfNeeded()
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        voiceEngine.speak(text, onDone)
    }

    fun stopSpeaking() {
        voiceEngine.stop()
    }

    private fun speakIntro() {
        speak("Jarvis operating core successfully initialized, Sir. Welcome back.")
    }

    // --- STREAM MULTIMODAL SIMULATORS ---

    fun analyzeCameraFeed(presetIndex: Int, presetTitle: String, imageB64: String) {
        if (_isCameraProcessing.value) return
        _isCameraProcessing.value = true
        _cameraStatusText.value = "Analyzing spectrum feed of $presetTitle..."
        
        viewModelScope.launch {
            val prompt = "Analyze this camera scan from JARVIS smart lenses. Report objects detected, context, and give a classy Jarvis style summary of what we are looking at. Refer to user as 'Sir'."
            val resp = repository.analyzeMultimodal(prompt, imageB64)
            _cameraStatusText.value = resp
            _isCameraProcessing.value = false
            speak(resp)
        }
    }

    fun analyzeScreenFeed(presetIndex: Int, presetTitle: String, imageB64: String) {
        if (_isScreenProcessing.value) return
        _isScreenProcessing.value = true
        _screenStatusText.value = "Reading dynamic UI coordinates on $presetTitle..."
        
        viewModelScope.launch {
            val prompt = "Read this Android UI screenshot. Locate buttons, text fields, headers, and summarize the page content. Give a clean, high-tech breakdown with specific elements found, in JARVIS voice. Call user 'Sir'."
            val resp = repository.analyzeMultimodal(prompt, imageB64)
            _screenStatusText.value = resp
            _isScreenProcessing.value = false
            speak(resp)
        }
    }

    // --- FULL PHONE AUTOMATION SIMULATOR ROUTINES ---

    fun runInstagramAutomation() {
        if (_isAutomationRunning.value) return
        _isAutomationRunning.value = true
        _currentScreen.value = "automation"

        val steps = listOf(
            AutomationStep("Package Lookup", "Scanning accessibility nodes for 'com.instagram.android'", "Running"),
            AutomationStep("Inject Screen View", "Navigating active user feeds and tapping 'Add Media' (+)", "Pending"),
            AutomationStep("Neural Caption Writer", "Calling Gemini logic to compose futuristic description and witty Stark hashtags", "Pending"),
            AutomationStep("Automation Posting", "Clicking 'Share' node and finalizing upload pipeline", "Pending")
        )
        _activeAutomationSteps.value = steps
        _automationProgressLog.value = listOf("[EXEC] Starting system task 'Instagram Media Publish'...")

        viewModelScope.launch {
            speak("Executing Instagram Automation routine, Sir. Please observe.")
            delay(2000)
            updateStep(0, "Success", "[NODE] Found package 'com.instagram.android' at memory location 0xACF24")
            addLog("[SYSTEM] Bypassing manual touches. Tapping element (ID: add_media_btn)...")
            
            delay(2000)
            updateStep(1, "Success", "[NODE] Media dashboard overlay loaded. Image selected successfully.")
            addLog("[AI] Triggering Gemini Caption synthesizers for Stark Labs imagery...")

            delay(2000)
            updateStep(2, "Success", "[AI] Text generated: 'Forging new frontiers. Thermal cores stabilized. #StarkLabs #PowerRecycled #IronMan'")
            addLog("[SYSTEM] Typing payload text into screen edit_text box... done.")

            delay(2000)
            updateStep(3, "Success", "[NODE] Tapping node 'Post Button' (0x7F2B)... Upload Complete!")
            addLog("[FINALIZE] Instagram Automated Flow Completed successfully, Sir!")
            _isAutomationRunning.value = false

            // Save execution to Room database
            saveAutomationLog("Instagram Post Caption Automator", "Success", _activeAutomationSteps.value)
            speak("Instagram post published successfully with AI generated hashtags, Sir.")
        }
    }

    fun runSpotifyAutomation() {
        if (_isAutomationRunning.value) return
        _isAutomationRunning.value = true
        _currentScreen.value = "automation"

        val steps = listOf(
            AutomationStep("Search Query", "Checking com.spotify.music package context and initiating search node", "Running"),
            AutomationStep("Category Navigation", "Opening Workout Genre dashboard and cataloging tracks", "Pending"),
            AutomationStep("Audio Playback", "Selecting 'Stark Industrial Workout Mix' and triggering key event 126", "Pending")
        )
        _activeAutomationSteps.value = steps
        _automationProgressLog.value = listOf("[EXEC] Starting Spotify playlist scheduler, Sir...")

        viewModelScope.launch {
            speak("Deploying workout audio playlist, Sir. Initiating Spotify.")
            delay(2000)
            updateStep(0, "Success", "[NODE] Spotify active instance validated. Focused on Search.")
            addLog("[SYSTEM] Locating query field and typing 'Stark Industrial'...")

            delay(2000)
            updateStep(1, "Success", "[DB] Found custom Stark training playlist. 48 high-energy tracks validated.")
            addLog("[COMMAND] Injecting Android AudioManager media playback broadcast...")

            delay(2000)
            updateStep(2, "Success", "[AUDIO] Track playing: 'Driven by AC/DC - Stark Remix'")
            addLog("[FINALIZE] Spotify automation finished. Enjoy your workout, Sir.")
            _isAutomationRunning.value = false

            saveAutomationLog("Spotify Workout Playlist Automation", "Success", _activeAutomationSteps.value)
            speak("Workout motivation playlist is now streaming on Spotify, Sir.")
        }
    }

    fun runWhatsAppAutomation() {
        if (_isAutomationRunning.value) return
        _isAutomationRunning.value = true
        _currentScreen.value = "automation"

        val steps = listOf(
            AutomationStep("Contacts Scan", "Querying provider for 'Rahul' and loading chat profile", "Running"),
            AutomationStep("Message Generation", "Preparing message payload: 'I will reach by 7 PM.'", "Pending"),
            AutomationStep("Accessibility Dispatch", "Tapping send node and archiving thread", "Pending")
        )
        _activeAutomationSteps.value = steps
        _automationProgressLog.value = listOf("[EXEC] Starting secure message delivery route...")

        viewModelScope.launch {
            speak("Initializing WhatsApp delivery pipeline to contact Rahul.")
            delay(2000)
            updateStep(0, "Success", "[CONTACTS] Rahul's terminal node identified and targeted.")
            addLog("[SYSTEM] Simulating background touch nodes in WhatsApp UI thread...")

            delay(2000)
            updateStep(1, "Success", "[AI] Formulating exact professional reply context: 'Hi Rahul, I will reach by 7 PM.'")
            addLog("[INPUT] Inserting text characters to conversation stream...")

            delay(2000)
            updateStep(2, "Success", "[SUCCESS] Sent successfully. Delivered checkmarks logged.")
            addLog("[FINALIZE] WhatsApp automation successfully dispatched. Connection offline.")
            _isAutomationRunning.value = false

            saveAutomationLog("WhatsApp Messenger - Send to Rahul", "Success", _activeAutomationSteps.value)
            speak("Message delivered. Rahul has been notified that you will reach by 7 PM, Sir.")
        }
    }

    private fun updateStep(index: Int, status: String, description: String) {
        val current = _activeAutomationSteps.value.toMutableList()
        if (index in current.indices) {
            val updatedStep = current[index].copy(status = status, description = description)
            current[index] = updatedStep
            _activeAutomationSteps.value = current
            addLog("[STEP ${index + 1}] $status - $description")

            // Mark the next step as running if any
            if (status == "Success" && (index + 1) in current.indices) {
                current[index + 1] = current[index + 1].copy(status = "Running")
                _activeAutomationSteps.value = current
            }
        }
    }

    private fun addLog(log: String) {
        _automationProgressLog.value = _automationProgressLog.value + log
    }

    private fun saveAutomationLog(command: String, status: String, steps: List<AutomationStep>) {
        viewModelScope.launch {
            val json = stepsAdapter.toJson(steps)
            repository.insertAutomation(
                JarvisAutomation(command = command, status = status, stepsListJson = json)
            )
        }
    }

    override fun onCleared() {
        voiceEngine.shutdown()
        super.onCleared()
    }
}
