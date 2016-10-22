package com.timursoft.catchthecat

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock

class OneShotAnimationDrawable(bitmap: Bitmap, frames: Int, duration: Int) : AbstractAnimationDrawable(bitmap, frames) {

    private val frameDuration = duration / frames

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

    override fun start() {
        frame = 0
        super.start()
    }
}