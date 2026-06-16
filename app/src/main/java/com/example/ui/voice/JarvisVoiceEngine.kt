package com.example.ui.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class JarvisVoiceEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _currentVoice = MutableStateFlow("Male Futuristic")
    val currentVoice: StateFlow<String> = _currentVoice

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = true
                setupProgressListener()
                applyVoiceSettings(_currentVoice.value)
            }
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun selectVoice(voiceName: String) {
        _currentVoice.value = voiceName
        applyVoiceSettings(voiceName)
    }

    private fun applyVoiceSettings(voiceName: String) {
        if (!isInitialized) return
        when (voiceName) {
            "Male Professional" -> {
                tts?.setPitch(0.85f)
                tts?.setSpeechRate(1.05f)
            }
            "Male Futuristic" -> {
                tts?.setPitch(0.70f)
                tts?.setSpeechRate(0.95f)
            }
            "Female Professional" -> {
                tts?.setPitch(1.30f)
                tts?.setSpeechRate(1.05f)
            }
            "Female Futuristic" -> {
                tts?.setPitch(1.15f)
                tts?.setSpeechRate(0.92f)
            }
            else -> {
                tts?.setPitch(1.00f)
                tts?.setSpeechRate(1.00f)
            }
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        if (!isInitialized) {
            onDone?.invoke()
            return
        }

        // Stop any current speech
        stop()

        val params = android.os.Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "jarvis_speech_id")
        }

        // Trigger speaks state in flow
        _isSpeaking.value = true
        
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "jarvis_speech_id")
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    fun shutdown() {
        if (isInitialized) {
            tts?.shutdown()
        }
    }
}
