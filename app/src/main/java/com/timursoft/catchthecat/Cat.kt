package com.timursoft.catchthecat

import android.animation.Animator
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.widget.ImageView

class Cat(res: Resources, var statusBarHeight: Int) {

    private val DEFAULT_DURATION: Long = 300

    private val idle: AnimationDrawable
    private val sleep: OneShotAnimationDrawable
    private val left_bottom: AnimationDrawable
    private val right_bottom: AnimationDrawable
    private val left_top: AnimationDrawable
    private val right_top: AnimationDrawable
    private val left: AnimationDrawable
    private val right: AnimationDrawable
    private val bottom: AnimationDrawable
    private val top: AnimationDrawable

    init {
        idle = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_idle), 8, 7)
        sleep = OneShotAnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_sleep), 13, 1500)

        val RUN_FRAME = 4
        val RUN_FPS = 8
        left_bottom = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_left_bottom), RUN_FRAME, RUN_FPS)
        right_bottom = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_right_bottom), RUN_FRAME, RUN_FPS)
        left_top = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_left_top), RUN_FRAME, RUN_FPS)
        right_top = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_right_top), RUN_FRAME, RUN_FPS)
        left = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_left), RUN_FRAME, RUN_FPS)
        right = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_right), RUN_FRAME, RUN_FPS)
        bottom = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_bottom), RUN_FRAME, RUN_FPS)
        top = AnimationDrawable(BitmapFactory.decodeResource(res, R.drawable.cat_top), RUN_FRAME, RUN_FPS)
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

    fun goSleep() {
        idle.stop()
        view!!.setImageDrawable(sleep)
        sleep.start()
    }

    fun goOut() {
        var dx: Int = 0
        var dy: Int = 0
        val drawable: AnimationDrawable
        if (x == 0) {
            drawable = left
            dx = view!!.width * -3
        } else if (y == 0) {
            drawable = top
            dy = view!!.height * -3
        } else if (x == MainActivity.Xs) {
            drawable = right
            dx = view!!.width * 3
        } else {
            drawable = bottom
            dy = view!!.height * 3
        }

        view!!.animate()
                .x(view!!.x + dx)
                .y(view!!.y + dy)
                .setDuration(MainActivity.END_DURATION)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animator: Animator) {
                    }

                    override fun onAnimationEnd(animator: Animator) {
                        drawable.stop()
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
