package com.timursoft.catchthecat

import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.SystemClock

class AssetAnimationDrawable(var bitmap: Bitmap, val frames: Int, fps: Int) : Drawable(), Runnable {

    private val bitmapPaint: Paint

    private val width: Int
    private val height: Int
    private val duration: Int

    private var lastUpdate: Long = 0
    private var frame: Int = 0
    private var frameRect: Rect

    init {
        width = bitmap.width / frames
        height = bitmap.height

        duration = 1000 / fps
        // division.
        // i.e. duration would be 33 for 30fps, meaning
        // 990ms for 30 frames.

        bitmapPaint = Paint()
        bitmapPaint.isFilterBitmap = true

        frame = 0
        frameRect = Rect(0, 0, width, height) // first frame
        lastUpdate = SystemClock.uptimeMillis()
    }// A.G.: note the little gap cause of integer

    // // TODO: 17.10.16
    //    @Override
    //    protected void finalize() throws Throwable {
    //        super.finalize();
    //        recycle();
    //    }

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
        val tick = SystemClock.uptimeMillis()

        if (tick - lastUpdate >= duration) {
            frame = (frame + (tick - lastUpdate) / duration).toInt() % frames
            lastUpdate = tick // TODO: time shift for incomplete frames

            frameRect = Rect(frame * width, 0, (frame + 1) * width, height)
            invalidateSelf()
        }

        scheduleSelf(this, tick + duration)
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