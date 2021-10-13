package com.hyejineee.recordapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class AudioVisualiserView
    (context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var onRequestCurrentAmplitude :(()->Int)? = null

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_W
        strokeCap = Paint.Cap.ROUND
    }

    var drawingAmplitudes = emptyList<Int>()
    var isReplaying = false
    var replayingPosition = 0

    var w:Int = 0
    var h:Int = 0

    private val visualiseRepeatAction : Runnable = object :Runnable{
        override fun run() {

            if(!isReplaying){
                val currentAmplitude = onRequestCurrentAmplitude?.invoke()?:0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            }else{
                replayingPosition++
            }

            invalidate() //데이터 갱신하고 뷰 다시 그려주는 작업.

            handler?.postDelayed(this, 20)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?: return

        val centerY = h/2F
        var offsetX = w.toFloat()

        drawingAmplitudes
            .let {
                if(isReplaying){
                    it.takeLast(replayingPosition)
                }else{
                    it
                }
            }
            .forEach {
            val lineH = it / MEX_AMPLITUDE * h * 0.8f

            offsetX -= LINE_SPACE

            if(offsetX < 0) return@forEach

            canvas.drawLine(
                offsetX, centerY - lineH /2F, offsetX, centerY + lineH/2F, paint
            )

        }
    }

    fun startVisualize(isReplaying:Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualiseRepeatAction)
    }

    fun stopVisualize(){
        handler?.removeCallbacks(visualiseRepeatAction)
    }

    fun clearVisualize(){
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object{
        private const val LINE_W = 10F
        private const val LINE_SPACE = 15F
        private const val MEX_AMPLITUDE = Short.MAX_VALUE.toFloat()
    }



}