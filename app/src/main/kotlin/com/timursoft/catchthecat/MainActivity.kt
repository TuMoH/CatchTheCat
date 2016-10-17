package com.timursoft.catchthecat

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.jetbrains.anko.*
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import org.xguzm.pathfinding.grid.finders.GridFinderOptions
import org.xguzm.pathfinding.grid.heuristics.ManhattanDistance
import java.util.*

class MainActivity : AppCompatActivity() {

    private val Xs = 10
    private val Ys = 10

    private val finder = AStarGridFinder(Cell::class.java, GridFinderOptions(true, false, ManhattanDistance(), false, 1f, 1f))

    private var init = true
    private var cat = Cat()
    private var rootLayout: View? = null
    private var cells: Array<Array<Cell?>> = Array(Ys + 1) { arrayOfNulls<Cell>(Xs + 1) }
    private var navGrid = CatNavigationGrid(cells)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    fun init() {
        cat.statusBarHeight = getStatusBarHeight()

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
                            setPadding(0, 0, dip(12), 0)
                        } else {
                            setPadding(dip(12), 0, 0, 0)
                        }

                        for (x in 0..Xs) {
                            val imageView = imageView {
                                lparams {
                                    weight = 1f
                                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                                }
                                imageResource = R.drawable.ic_cell

                                onClick {
                                    cells[x][y]?.check()
                                    if (isEnd()) {
                                        showEndDialog(R.string.defeat_msg)
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
                val bitmap = BitmapFactory.decodeStream(assets.open("sprites/boss6/boss6_cry.png"))
                val animation = AssetAnimationDrawable(bitmap, 6, 10)
                setImageDrawable(animation)
                animation.start()
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
                    rootLayout!!.animate().alpha(1f).duration = 1000
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
            showEndDialog(R.string.win_msg)
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

    fun showEndDialog(msgId: Int) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show()
        rootLayout!!.animate()
                .alpha(0f)
                .setDuration(1000)
                .withEndAction { restart() }
    }

    fun restart() {
        init = true
        cat = Cat()
        cells = Array(Ys + 1) { arrayOfNulls<Cell>(Xs + 1) }
        navGrid = CatNavigationGrid(cells)

        init()
    }

}
