package com.timursoft.catchthecat

import android.graphics.*
import android.graphics.drawable.Drawable

abstract class AbstractAnimationDrawable(var bitmap: Bitmap, val frames: Int) : Drawable(), Runnable {

    protected val bitmapPaint = Paint()

    protected val width = bitmap.width / frames
    protected val height = bitmap.height

    protected var frame = 0
    protected var frameRect = Rect(0, 0, width, height)

    init {
        bitmapPaint.isFilterBitmap = true
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

    //    @Override
    //    protected void finalize() throws Throwable {
    //        super.finalize();
    //        recycle();
    //    }

}
