package com.hyejineee.recordapp

import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(
    context: Context, attrs: AttributeSet
) : AppCompatTextView(context, attrs) {

    private var startTimeStamp:Long = 0L
    private val countUpAction: Runnable = object :Runnable{
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()

            val currentTimeSeconds = ((currentTimeStamp - startTimeStamp)/1000L).toInt()
            updateCountTime(currentTimeSeconds)

            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp(){
        startTimeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUpAction)
    }

    fun stopCountUp(){
        handler?.removeCallbacks(countUpAction)
    }

    private fun updateCountTime(countTime:Int){
        val m = countTime/60
        val s = countTime%60

        text = "%02d:%02d".format(m,s)
    }
}