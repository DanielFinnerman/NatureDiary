package com.example.naturediary

import android.content.Context
import android.media.*
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class Recorder {

    companion object {
        lateinit var mainContext: Context
        lateinit var currentFile: File

        private var isRecording = false
        private var isPlaying = false

        fun init(context: Context) {
            mainContext = context
        }
    }

    fun record() {
        val storageDir = mainContext.getExternalFilesDir((Environment.DIRECTORY_MUSIC))
        try {
            currentFile = File("${storageDir.toString()}/${System.currentTimeMillis()}")
        } catch (e: IOException) {
            Log.d(TAG, "recFile error: $e")
        }

        try {
            val outStream = FileOutputStream(currentFile)
            val bufferedOutStream = BufferedOutputStream(outStream)
            val dataOutSteam = DataOutputStream(bufferedOutStream)

            val minBufferSize =
                AudioRecord.getMinBufferSize(
                    44100,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

            val audioFormat =
                AudioFormat.Builder()
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(44100)
                    .build()

            val recorder =
                AudioRecord.Builder()
                    .setAudioSource(MediaRecorder.AudioSource.MIC)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(minBufferSize)
                    .build()

            val audioData = ByteArray(minBufferSize)

            GlobalScope.launch(Dispatchers.IO) {
                isRecording = true
                recorder.startRecording()
                while (isRecording) {
                    val numOfBytes = recorder.read(audioData, 0, minBufferSize)
                    if (numOfBytes > 0) {
                        dataOutSteam.write(audioData)
                    }
                }
                recorder.stop()
                dataOutSteam.close()
            }

        } catch (e: IOException) {
            Log.d(TAG, "err: $e")
        }
    }

    fun play() {
        val stream = FileInputStream(currentFile)
        try {
            GlobalScope.launch(Dispatchers.Main) {
                isPlaying = true
                withContext(Dispatchers.IO) {
                    val minBufferSize =
                        AudioTrack.getMinBufferSize(
                            44100,
                            AudioFormat.CHANNEL_OUT_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT
                        )
                    val audioBuilder = AudioTrack.Builder()
                    val audioAttr = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                    val audioFormat = AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                    val track = audioBuilder
                        .setAudioAttributes(audioAttr)
                        .setAudioFormat(audioFormat)
                        .setBufferSizeInBytes(minBufferSize)
                        .build()

                    track.setVolume(0.8f)
                    track.play()

                    val buffer = ByteArray(minBufferSize)

                    try {
                        var time = stream.read(buffer, 0, minBufferSize)
                        while (time != -1 && isPlaying) {
                            track.write(buffer, 0, time)
                            time = stream.read(buffer, 0, minBufferSize)
                        }
                    } catch (e: IOException) {
                        Log.d(TAG, "$e")
                    }

                    try {
                        stream.close()
                    } catch (e: IOException) {
                        Log.d(TAG, "$e")
                    }

                    track.stop()
                    track.release()
                }
            }
        } catch (e: IOException) {
            Log.d(TAG, "$e")
        }
    }

    fun stop() {
        isRecording = false
    }
}