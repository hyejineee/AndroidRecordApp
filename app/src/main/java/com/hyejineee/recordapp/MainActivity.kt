package com.hyejineee.recordapp

import android.Manifest.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hyejineee.recordapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding

    private var state = RecordingState.BEFORE_RECORDING
        set(value) {
            field = value
            activityMainBinding.resetButton.isEnabled =
                (value == RecordingState.AFTER_RECORDING || value == RecordingState.ON_PLAYING)
            activityMainBinding.recordButton.setImageResourceWithState(value)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                Toast.makeText(this, "녹음 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                activityMainBinding.recordButton.isEnabled = false
                return@registerForActivityResult
            }
        }

    private var recorder: MediaRecorder? = null
    private val recordingFilePath: String? by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private var player: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        checkPermissions()

        initViews()
        initState()

        handleRecordButton()
        handleResetButton()
    }

    private fun initState(){
        state = RecordingState.BEFORE_RECORDING
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_DENIED
        ) {
            permissionLauncher.launch(permission.RECORD_AUDIO)
            return
        }
    }

    private fun initViews() {
        activityMainBinding.recordButton.setImageResourceWithState(state)
        activityMainBinding.recordingVisualView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude?:0
        }
    }

    private fun handleRecordButton() {
        activityMainBinding.recordButton.setOnClickListener {
            when (state) {
                RecordingState.BEFORE_RECORDING -> {
                    startRecording()
                }
                RecordingState.ON_RECORDING -> {
                    stopRecording()
                }
                RecordingState.AFTER_RECORDING -> {
                    startPlaying()
                }
                RecordingState.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }
    }

    private fun handleResetButton() {
        activityMainBinding.resetButton.setOnClickListener {
            stopPlaying()
            state = RecordingState.BEFORE_RECORDING
            activityMainBinding.recordingVisualView.clearVisualize()
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }

        recorder?.start()
        state = RecordingState.ON_RECORDING
        activityMainBinding.recordingVisualView.startVisualize(false)
        activityMainBinding.recordTimeTextView.startCountUp()
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null

        state = RecordingState.AFTER_RECORDING
        activityMainBinding.recordingVisualView.stopVisualize()
        activityMainBinding.recordTimeTextView.stopCountUp()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepareAsync()
        }

        player?.start()
        state = RecordingState.ON_PLAYING
        activityMainBinding.recordingVisualView.startVisualize(true)
        activityMainBinding.recordTimeTextView.startCountUp()

    }

    private fun stopPlaying() {
        player?.release()
        player = null
        state = RecordingState.AFTER_RECORDING
        activityMainBinding.recordingVisualView.stopVisualize()
        activityMainBinding.recordTimeTextView.stopCountUp()
    }
}