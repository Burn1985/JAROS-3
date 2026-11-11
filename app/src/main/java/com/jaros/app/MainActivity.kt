
package com.jaros.app

import android.app.Activity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.tts.TextToSpeech
import android.content.Intent
import android.widget.*
import android.view.Gravity
import android.graphics.Color
import android.view.ViewGroup
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {

    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null

    private lateinit var status: TextView
    private lateinit var result: TextView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(32, 60, 32, 32)
            setBackgroundColor(Color.rgb(11, 24, 42))
        }

        val title = TextView(this).apply {
            text = "ЖАРОС"
            textSize = 34f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "Универсальная платформа управления"
            textSize = 15f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "Система готова"
            textSize = 18f
            setTextColor(Color.GREEN)
            gravity = Gravity.CENTER
        }

        button = Button(this).apply {
            text = "🎙 ГОВОРИТЬ С ЖАРОС"
            textSize = 18f
            setOnClickListener { startListening() }
        }

        result = TextView(this).apply {
            text = "Здесь появится распознанная речь"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 35, 0, 20)
        }

        layout.addView(title)
        layout.addView(subtitle)

        layout.addView(status, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 100
        ))

        layout.addView(button, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 75
        ))

        layout.addView(result)

        setContentView(layout)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO), 100
            )
        }

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this)
            recognizer?.setRecognitionListener(listener)
        } else {
            status.text = "Распознавание речи недоступно"
        }
    }

    private fun startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO), 100
            )
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "ru-RU"
        )

        status.text = "Слушаю..."
        recognizer?.startListening(intent)
    }

    private val listener = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            status.text = "Готов слушать"
        }

        override fun onBeginningOfSpeech() {
            status.text = "Распознаю речь..."
        }

        override fun onEndOfSpeech() {
            status.text = "Обрабатываю..."
        }

        override fun onResults(results: Bundle?) {
            val words = results?.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION
            )

            val command = words?.firstOrNull() ?: "Не удалось распознать"

            result.text = "Вы сказали:\n$command"
            processCommand(command)
        }

        override fun onError(error: Int) {
            status.text = "Ошибка распознавания: $error"
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun processCommand(command: String) {
        val answer = when {
            command.contains("привет", true) ->
                "Привет! Я ЖАРОС. Слушаю тебя."

            command.contains("как дела", true) ->
                "Система работает. Готов к командам."

            command.contains("Google", true) ->
                "Команда открытия Google распознана. Исполнитель пока не подключён."

            command.contains("погода", true) ->
                "Запрос погоды распознан. Погодный сервис пока не подключён."

            else ->
                "Я получил твою команду. Интеллектуальное ядро будет подключено следующим этапом."
        }

        result.append("\n\nЖАРОС: $answer")
        status.text = "Команда принята"
        tts?.speak(answer, TextToSpeech.QUEUE_FLUSH, null, "jaros")
    }

    override fun onInit(code: Int) {
        if (code == TextToSpeech.SUCCESS) {
            tts?.language = Locale("ru", "RU")
        }
    }

    override fun onDestroy() {
        recognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
