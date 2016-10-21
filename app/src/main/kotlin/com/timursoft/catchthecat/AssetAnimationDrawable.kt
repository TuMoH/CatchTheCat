package com.timursoft.catchthecat

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock

class AssetAnimationDrawable(bitmap: Bitmap, frames: Int, fps: Int) : AbstractAnimationDrawable(bitmap, frames) {

    private val duration = 1000 / fps
    // division.
    // i.e. duration would be 33 for 30fps, meaning
    // 990ms for 30 frames.
    private var lastUpdate = SystemClock.uptimeMillis()
    // A.G.: note the little gap cause of integer

    override fun run() {
        val tick = SystemClock.uptimeMillis()

        if (tick - lastUpdate >= duration) {
            frame = (frame + (tick - lastUpdate) / duration).toInt() % frames
            lastUpdate = tick

            frameRect = Rect(frame * width, 0, (frame + 1) * width, height)
            invalidateSelf()
        }

        scheduleSelf(this, tick + duration)
    }

}