package com.timursoft.catchthecat

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock

class AnimationDrawable(bitmap: Bitmap, frames: Int, fps: Int) : AbstractAnimationDrawable(bitmap, frames) {

    private val duration = 1000 / fps

    override fun run() {
        frame++

        if (frame == frames) {
            frame = 0
        }

        frameRect = Rect(frame * width, 0, (frame + 1) * width, height)
        invalidateSelf()

        val tick = SystemClock.uptimeMillis()
        scheduleSelf(this, tick + duration)
    }

}