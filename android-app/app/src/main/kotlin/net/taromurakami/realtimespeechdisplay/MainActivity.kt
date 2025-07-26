package net.taromurakami.realtimespeechdisplay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var recognizedText: TextView
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var recognizerIntent: Intent
    private lateinit var firestore: FirebaseFirestore
    
    private var isRecording = false
    private var isInitialized = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            initializeSpeechRecognizer()
        } else {
            Toast.makeText(this, getString(R.string.error_permission), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        initializeFirebase()
        checkPermissions()
    }
    
    private fun initializeViews() {
        statusText = findViewById(R.id.statusText)
        recognizedText = findViewById(R.id.recognizedText)
        startRecordingButton = findViewById(R.id.startRecordingButton)
        stopRecordingButton = findViewById(R.id.stopRecordingButton)
        
        startRecordingButton.setOnClickListener { startListening() }
        stopRecordingButton.setOnClickListener { stopListening() }
    }
    
    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
    }
    
    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                initializeSpeechRecognizer()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    
    private fun initializeSpeechRecognizer() {
        createSpeechRecognizer()
        
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN") // Mandarin Chinese
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        isInitialized = true
    }
    
    private fun createSpeechRecognizer() {
        // Clean up existing recognizer
        speechRecognizer?.destroy()
        
        // Create new recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                statusText.text = getString(R.string.recording)
            }
            
            override fun onBeginningOfSpeech() {
                statusText.text = getString(R.string.recording)
            }
            
            override fun onRmsChanged(rmsdB: Float) {}
            
            override fun onBufferReceived(buffer: ByteArray?) {}
            
            override fun onEndOfSpeech() {
                statusText.text = getString(R.string.processing)
            }
            
            override fun onError(error: Int) {
                statusText.text = getString(R.string.error_speech_recognition)
                
                // Recreate the speech recognizer for next use
                if (isRecording) {
                    // Restart listening with a new recognizer
                    createSpeechRecognizer()
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        if (isRecording) {
                            speechRecognizer?.startListening(recognizerIntent)
                        }
                    }, 100) // Small delay to ensure proper cleanup
                } else {
                    resetButtons()
                }
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedSpeech = matches[0]
                    handleRecognizedSpeech(recognizedSpeech)
                }
                
                // Continue listening if still recording - recreate recognizer first
                if (isRecording) {
                    createSpeechRecognizer()
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        if (isRecording) {
                            speechRecognizer?.startListening(recognizerIntent)
                        }
                    }, 100) // Small delay to ensure proper cleanup
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val currentText = recognizedText.text.toString()
                    val partialText = matches[0]
                    recognizedText.text = "$currentText $partialText"
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
    
    private fun startListening() {
        if (!isRecording && isInitialized) {
            isRecording = true
            startRecordingButton.isEnabled = false
            stopRecordingButton.isEnabled = true
            
            // Ensure we have a fresh recognizer
            createSpeechRecognizer()
            speechRecognizer?.startListening(recognizerIntent)
        }
    }
    
    private fun stopListening() {
        if (isRecording) {
            isRecording = false
            speechRecognizer?.stopListening()
            resetButtons()
        }
    }
    
    private fun resetButtons() {
        startRecordingButton.isEnabled = true
        stopRecordingButton.isEnabled = false
        statusText.text = getString(R.string.app_ready)
    }
    
    private fun handleRecognizedSpeech(speech: String) {
        val currentText = recognizedText.text.toString()
        val newText = if (currentText == getString(R.string.speech_will_appear_here)) {
            speech
        } else {
            "$currentText\n$speech"
        }
        
        recognizedText.text = newText
        
        // Save to Firebase
        saveToFirestore(speech)
    }
    
    private fun saveToFirestore(text: String) {
        val speechEntry = hashMapOf(
            "text" to text,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "language" to "zh-CN"
        )
        
        firestore.collection("speechEntries")
            .add(speechEntry)
            .addOnSuccessListener { documentReference ->
                // Successfully saved to Firebase
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        speechRecognizer?.destroy()
    }
}