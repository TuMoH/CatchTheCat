package com.timursoft.catchthecat

import android.widget.ImageView

class Cat {

    var x: Int = 0
    var y: Int = 0
    lateinit var view: ImageView

    var statusBarHeight = 0

    fun move(cell: Cell) {
        x = cell.x
        y = cell.y

        val location = IntArray(2)
        cell.view.getLocationOnScreen(location)
        val offsetX = (cell.view.width - view.width).toFloat() / 2
        val offsetY = (cell.view.height - view.height).toFloat() / 2
        view.x = location[0].toFloat() + offsetX
        view.y = location[1].toFloat() + offsetY - statusBarHeight

        // todo add animation
    }

}
