package com.timursoft.catchthecat

import android.animation.Animator
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.widget.ImageView

class Cat(res: Resources, var statusBarHeight: Int) {

    private val DEFAULT_DURATION: Long = 300

    private val idle: AnimationDrawable
    private val left_bottom: AnimationDrawable
    private val right_bottom: AnimationDrawable
    private val left_top: AnimationDrawable
    private val right_top: AnimationDrawable
    private val left: AnimationDrawable
    private val right: AnimationDrawable
    private val IDLE_FRAME: Int
    private val IDLE_FPS: Int
    private val RUN_FRAME: Int
    private val RUN_FPS: Int

    init {
        IDLE_FRAME = 8
        IDLE_FPS = 7
        idle = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_idle), IDLE_FRAME, IDLE_FPS)

        RUN_FRAME = 4
        RUN_FPS = 8
        left_bottom = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_left_bottom), RUN_FRAME, RUN_FPS)
        right_bottom = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_right_bottom), RUN_FRAME, RUN_FPS)
        left_top = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_left_top), RUN_FRAME, RUN_FPS)
        right_top = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_right_top), RUN_FRAME, RUN_FPS)
        left = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_left), RUN_FRAME, RUN_FPS)
        right = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_right), RUN_FRAME, RUN_FPS)
    }

    var x: Int = 0
    var y: Int = 0
    var view: ImageView? = null

    fun move(cell: Cell) {
        move(cell, DEFAULT_DURATION)
    }

    fun move(cell: Cell, duration: Long) {
        x = cell.x
        y = cell.y

        val location = IntArray(2)
        cell.view.getLocationOnScreen(location)
        val offsetX = (cell.view.width - view!!.width).toFloat() / 2
        val offsetY = (cell.view.height - view!!.height).toFloat() / 2

        val dx = location[0] - view!!.x
        val dy = location[1] - statusBarHeight - view!!.y
        val drawable: AnimationDrawable
        val toLeft = dx < 0
        if (dy > 0) {
            if (toLeft) {
                drawable = left_bottom
            } else {
                drawable = right_bottom
            }
        } else if (dy < 0) {
            if (toLeft) {
                drawable = left_top
            } else {
                drawable = right_top
            }
        } else {
            if (toLeft) {
                drawable = left
            } else {
                drawable = right
            }
        }

        view!!.animate()
                .x(location[0].toFloat() + offsetX)
                .y(location[1].toFloat() + offsetY - statusBarHeight)
                .setDuration(duration)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animator: Animator) {
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        drawable.stop()
                        view!!.setImageDrawable(idle)
                        idle.start()
                    }

                    override fun onAnimationStart(animator: Animator) {
                        idle.stop()
                        view!!.setImageDrawable(drawable)
                        drawable.start()
                    }

                    override fun onAnimationCancel(animator: Animator) {
                    }
                })
    }

}
