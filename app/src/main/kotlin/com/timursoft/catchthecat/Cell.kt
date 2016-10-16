package com.timursoft.catchthecat

import android.widget.ImageView
import org.xguzm.pathfinding.grid.GridCell

class Cell(x: Int, y: Int, val view: ImageView) : GridCell(x, y) {

    fun check() {
        isWalkable = false

        view.isClickable = false
        view.setColorFilter(view.resources.getColor(R.color.activated_cell))
    }

}