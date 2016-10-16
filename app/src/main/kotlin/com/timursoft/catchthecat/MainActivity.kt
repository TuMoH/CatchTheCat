package com.timursoft.catchthecat

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.*
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import org.xguzm.pathfinding.grid.heuristics.ManhattanDistance
import java.util.*

class MainActivity : AppCompatActivity() {

    private var cat = Cat()
    private var init = true

    private lateinit var tv: TextView

    private val Xs = 10
    private val Ys = 10
    private var cells: Array<Array<Cell?>> = Array(Ys + 1) { arrayOfNulls<Cell>(Xs + 1) }

    private val finder = AStarGridFinder(Cell::class.java, GridFinderOptions(true, false, ManhattanDistance(), false, 1f, 1f))
    private val navGrid = CatNavigationGrid(cells)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cat.statusBarHeight = getStatusBarHeight()

        frameLayout {
            lparams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            tv = textView {}

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
                            setPadding(0, 0, dip(24), 0)
                        } else {
                            setPadding(dip(24), 0, 0, 0)
                        }

                        for (x in 0..Xs) {
                            val imageView = imageView {
                                lparams {
                                    weight = 1f
                                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                                }
//                                adjustViewBounds = true
                                imageResource = R.drawable.ic_cell

                                onClick {
                                    cells[x][y]?.check()
                                    if (isEnd()) {
                                        showEndDialog(R.string.defeat_title)
                                    } else {
                                        val path = findPath()
                                        if (path != null) {
                                            cat.move(path)
                                        }
                                    }
                                }
                            }
                            cells[x][y] = Cell(x, y, imageView)
                        }
                    }
                }
            }

            cat.view = imageView {
                lparams {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                isClickable = true
                // todo change resource
                imageResource = R.drawable.ic_smile
            }

            addOnLayoutChangeListener { view, i, k, l, j, h, g, f, d ->
                if (init) {
                    init = false
                    cat.move(cells[Ys / 2][Xs / 2]!!, 0)

                    val random = Random()
                    var needCheck = random.nextInt(12 - 7) + 7
                    while (needCheck > 0) {
                        val x = random.nextInt(Xs)
                        val y = random.nextInt(Ys)
                        if (x !in 4..6 && y !in 4..6) {
                            cells[x][y]?.check()
                            needCheck--
                        }
                    }
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

    fun isEnd(): Boolean {
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
            showEndDialog(R.string.win_title)
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

    fun showEndDialog(titleId: Int) {
        AlertDialog.Builder(this@MainActivity)
                .setTitle(titleId)
                .setMessage(R.string.defeat_msg)
                .setNegativeButton(R.string.defeat_no, { dialog, i ->
                    finish()
                })
                .setPositiveButton(R.string.defeat_yes, { dialog, i ->
                    recreate()
                })
                .create()
                .show()
    }

}
