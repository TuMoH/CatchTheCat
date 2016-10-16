package com.timursoft.catchthecat

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import org.jetbrains.anko.*
import org.xguzm.pathfinding.grid.NavigationGrid
import org.xguzm.pathfinding.grid.finders.AStarGridFinder
import java.util.*

class MainActivity : AppCompatActivity() {

    private var cat = Cat()
    private var init = true

    private val Xs = 10
    private val Ys = 10
    private var cells: Array<Array<Cell?>> = Array(Xs + 1) { arrayOfNulls<Cell>(Ys + 1) }

    private val finder = AStarGridFinder(Cell::class.java)
    private val navGrid = NavigationGrid(cells, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cat.statusBarHeight = getStatusBarHeight()

        frameLayout {
            lparams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            verticalLayout {
                lparams {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                gravity = Gravity.CENTER

                for (i in 0..Xs) {
                    linearLayout {
                        lparams {
                            width = ViewGroup.LayoutParams.WRAP_CONTENT
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }

                        if (i % 2 == 0) {
                            setPadding(0, 0, dip(24), 0)
                        } else {
                            setPadding(dip(24), 0, 0, 0)
                        }

                        for (j in 0..Ys) {
                            val imageView = imageView {
                                lparams {
                                    weight = 1f
                                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                                }
//                                adjustViewBounds = true
                                imageResource = R.drawable.ic_cell

                                onClick {
                                    cells[i][j]?.check()
                                    if (isEnd()) {
                                        AlertDialog.Builder(this@MainActivity)
                                                .setTitle(R.string.defeat_title)
                                                .setMessage(R.string.defeat_msg)
                                                .setNegativeButton(R.string.defeat_no, { dialog, i ->
                                                    finish()
                                                })
                                                .setPositiveButton(R.string.defeat_yes, { dialog, i ->
                                                    recreate()
                                                })
                                                .create()
                                                .show()
                                    } else {
                                        cat.move(findPath())
                                    }
                                }
                            }
                            cells[i][j] = Cell(i, j, imageView)
                        }
                    }
                }
            }

            cat.view = imageView {
                lparams {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                // todo change resource
                imageResource = R.drawable.ic_smile
            }

            addOnLayoutChangeListener { view, i, k, l, j, h, g, f, d ->
                if (init) {
                    init = false
                    cat.move(cells[5][5]!!)

                    val random = Random()
                    var needCheck = random.nextInt(12 - 7) + 7
                    while (needCheck > 0) {
                        val x = random.nextInt(10)
                        val y = random.nextInt(10)
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
        return cat.x == Xs || cat.y == Ys
    }

    fun findPath(): Cell {
        // todo find path
        // todo не бежит на крест при блоках по бокам
        val path = finder.findPath(cat.x, cat.y, Xs, Ys, navGrid)

        val next = path[0]

        // todo check win

        return cells[next.x][next.y]!!
    }

}
