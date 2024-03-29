package com.timursoft.catchthecat

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.*
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import org.xguzm.pathfinding.grid.heuristics.ManhattanDistance
import java.util.*

class MainActivity : Activity() {

    companion object {
        val Xs = 10
        val Ys = 10
        val END_DURATION: Long = 1000
    }

    private val CELL_RES = R.drawable.cell
    private val CELL_FRAME = 6

    private val finder = AStarGridFinder(
        Cell::class.java,
        GridFinderOptions(true, false, ManhattanDistance(), false, 1f, 1f)
    )
    private var statusBarHeight = 0
    private var cellWidth = 24
    private var cellHeight = 20
    private lateinit var cellBitmap: Bitmap

    private lateinit var mediaPlayer: MediaPlayer

    private var init = true
    private var initCells = true
    private lateinit var cat: Cat
    private lateinit var rootLayout: View
    private lateinit var cells: Array<Array<Cell?>>
    private lateinit var navGrid: CatNavigationGrid

    private var isWin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cellBitmap = BitmapFactory.decodeResource(resources, CELL_RES)

        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        mediaPlayer.isLooping = true

        statusBarHeight = getStatusBarHeight()
        cat = Cat(resources, statusBarHeight)

        val displayMetrics = resources.displayMetrics
        val dpHeight = (displayMetrics.heightPixels - statusBarHeight) / displayMetrics.density
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        cellWidth = (dpHeight / Ys).toInt()
        if (cellWidth * 11 > dpWidth) {
            cellWidth = (dpWidth / 11).toInt()
        }
        cellHeight = (cellWidth * 0.85f).toInt()

        init()
    }

    fun init() {
        init = true
        initCells = true
        cells = Array(Ys + 1) { arrayOfNulls<Cell>(Xs + 1) }
        navGrid = CatNavigationGrid(cells)

        rootLayout = frameLayout {
            lparams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            alpha = 0f

            verticalLayout {
                lparams {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                gravity = Gravity.CENTER

                for (y in 0..Ys) {
                    linearLayout {
                        lparams {
                            width = ViewGroup.LayoutParams.WRAP_CONTENT
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }

                        if (y % 2 == 0) {
                            setPadding(0, 0, dip(cellWidth / 2), 0)
                        } else {
                            setPadding(dip(cellWidth / 2), 0, 0, 0)
                        }

                        for (x in 0..Xs) {
                            val imageView = imageView {
                                scaleType = ImageView.ScaleType.FIT_XY
                                setImageDrawable(
                                    OneShotAnimationDrawable(
                                        cellBitmap,
                                        CELL_FRAME,
                                        200
                                    )
                                )

                                setOnClickListener {
                                    cells[x][y]?.check()
                                    if (isFail()) {
                                        fail()
                                    } else {
                                        val path = findPath()
                                        if (path != null) {
                                            cat.move(path)
                                        }
                                    }
                                }
                            }.lparams {
                                weight = 1f
                                width = dip(cellWidth)
                                height = dip(cellHeight)
                            }
                            cells[x][y] = Cell(x, y, imageView)
                        }
                    }
                }
            }

            cat.view = imageView {
                scaleType = ImageView.ScaleType.FIT_XY
                isClickable = true
            }.lparams {
                width = dip(cellWidth)
                height = dip(cellHeight)
            }

            addOnLayoutChangeListener { view, i, k, l, j, h, g, f, d ->
                if (init) {
                    init = false

                    val rect = Rect()
                    rootLayout.getWindowVisibleDisplayFrame(rect)
                    cat.statusBarHeight = rect.top

                    cat.move(cells[Ys / 2][Xs / 2]!!, 0)

                    if (initCells) {
                        initCells = false

                        val random = Random()
                        var needCheck = random.nextInt(12 - 8) + 8
                        while (needCheck > 0) {
                            val x = random.nextInt(Xs)
                            val y = random.nextInt(Ys)
                            if (x !in 4..6 && y !in 4..6) {
                                cells[x][y]?.check()
                                needCheck--
                            }
                        }
                    }
                    rootLayout.animate().alpha(1f).duration = 1000
                }
            }
        }
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun isFail(): Boolean {
        return cat.x == 0 || cat.y == 0 || cat.x == Xs || cat.y == Ys
    }

    fun findPath(): Cell? {
        var path: List<Cell>? = null

        for (x in 0..Xs) {
            path = findShortPath(path, x, 0)
            path = findShortPath(path, x, Ys)
        }
        for (y in 1..Ys - 1) {
            path = findShortPath(path, 0, y)
            path = findShortPath(path, Xs, y)
        }

        if (path == null) {
            win()
            return null
        }

        val nextCell = path[0]
        return cells[nextCell.x][nextCell.y]!!
    }

    fun findShortPath(lastShortPath: List<Cell>?, x: Int, y: Int): List<Cell>? {
        val path = finder.findPath(cat.x, cat.y, x, y, navGrid) ?: return lastShortPath
        if (lastShortPath == null) {
            return path.toList()
        }
        if (path.size < lastShortPath.size) {
            return path.toList()
        }
        return lastShortPath
    }

    fun win() {
        cat.goSleep()
        Snackbar.make(rootLayout, "Congratulations!", LENGTH_INDEFINITE)
            .setAction("Restart") { restartGame(0) }
            .show()
    }

    fun fail() {
        cat.goOut()
        restartGame(0)
    }

    fun restartGame(startDelay: Long) {
        rootLayout.animate()
            .alpha(0f)
            .setStartDelay(startDelay)
            .setDuration(END_DURATION)
            .withEndAction {
                init()
            }
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }
}
