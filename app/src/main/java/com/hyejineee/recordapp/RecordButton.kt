package com.hyejineee.recordapp

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

class RecordButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    fun setImageResourceWithState(state: RecordingState) {
        when (state) {
            RecordingState.BEFORE_RECORDING -> setImageResource(R.drawable.record_button_image)
            RecordingState.ON_RECORDING -> setImageResource(R.drawable.stop_button_image)
            RecordingState.AFTER_RECORDING -> setImageResource(R.drawable.play_button_image)
            RecordingState.ON_PLAYING -> setImageResource(R.drawable.stop_button_image)
        }
    }

}