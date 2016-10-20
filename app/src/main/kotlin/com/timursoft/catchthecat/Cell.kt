package com.timursoft.catchthecat

import android.widget.ImageView
import org.xguzm.pathfinding.grid.GridCell

class Cell(x: Int, y: Int, val view: ImageView) : GridCell(x, y) {

    val even: Boolean

    init {
        even = y % 2 == 0
    }

    fun check() {
        isWalkable = false

        view.isClickable = false
        (view.drawable as OneShotAnimationDrawable).start()
    }

}