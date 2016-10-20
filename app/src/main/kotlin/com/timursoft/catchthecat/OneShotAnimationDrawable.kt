package com.timursoft.catchthecat

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.SystemClock

class OneShotAnimationDrawable(var bitmap: Bitmap, val frames: Int, duration: Int) : Drawable(), Runnable {

    private val bitmapPaint: Paint

    private val width: Int
    private val height: Int

    private var frame: Int = 0
    private var frameRect: Rect
    private val frameDuration = duration / frames

    init {
        width = bitmap.width / frames
        height = bitmap.height

        bitmapPaint = Paint()
        bitmapPaint.isFilterBitmap = true

        frame = 0
        frameRect = Rect(0, 0, width, height)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, frameRect, copyBounds(), bitmapPaint)
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setAlpha(a: Int) {
        bitmapPaint.alpha = a
    }

    override fun setColorFilter(filter: ColorFilter?) {
        bitmapPaint.colorFilter = filter
    }

    override fun getIntrinsicWidth(): Int {
        return width
    }

    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun run() {
        frame++

        if (frame == frames) {
            stop()
            return
        }

        frameRect = Rect(frame * width, 0, (frame + 1) * width, height)
        invalidateSelf()

        val tick = SystemClock.uptimeMillis()
        scheduleSelf(this, tick + frameDuration)
    }

    fun start() {
        run()
    }

    fun stop() {
        unscheduleSelf(this)
    }

    fun recycle() {
        stop()
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

}