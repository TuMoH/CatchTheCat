package com.timursoft.catchthecat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import org.jetbrains.anko.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var cat: ImageView
    private var statusBarHeight = 0
    private var init = true

    private val ROW = 11
    private val CELL = 11
    private var tails: Array<Array<ImageView?>> = Array(ROW) { arrayOfNulls<ImageView>(CELL) }
    private var checkedTails: Array<BooleanArray> = Array(ROW) { BooleanArray(CELL) }

    private var currentPosition: IntArray = intArrayOf((ROW - 1) / 2, (CELL - 1) / 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        statusBarHeight = getStatusBarHeight()

        frameLayout {
            lparams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            button {
                lparams {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                text = "reset"
                onClick {
                    recreate()
                }
            }

            verticalLayout {
                lparams {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                gravity = Gravity.CENTER

                for (i in 0..ROW - 1) {
                    linearLayout {
                        lparams {
                            width = ViewGroup.LayoutParams.MATCH_PARENT
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                        }

                        if (i % 2 == 0) {
                            setPadding(0, 0, dip(10), 0)
                        } else {
                            setPadding(dip(10), 0, 0, 0)
                        }

                        for (j in 0..CELL - 1) {
                            tails[i][j] = imageView {
                                lparams {
                                    weight = 1f
                                    width = ViewGroup.LayoutParams.MATCH_PARENT
                                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                                }
                                adjustViewBounds = true
                                imageResource = R.drawable.ic_lens_black_24dp

                                onClick {
                                    checkTail(i, j)
                                    findPathAndMoveCat()
                                }
                            }
                        }
                    }
                }
            }

            cat = imageView {
                lparams {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                // todo change resource
                imageResource = R.drawable.ic_insert_emoticon_black_24dp
            }

            addOnLayoutChangeListener { view, i, k, l, j, h, g, f, d ->
                if (init) {
                    init = false
                    moveCat(tails[5][5])

                    val random = Random()
                    var needCheck = random.nextInt(12 - 7) + 7
                    while (needCheck > 0) {
                        val row = random.nextInt(10)
                        val cell = random.nextInt(10)
                        if (row !in 4..6 && cell !in 4..6) {
                            checkTail(row, cell)
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

    fun checkTail(row: Int, cell: Int) {
        val view = tails[row][cell]!!

        view.isClickable = false
        view.setColorFilter(resources.getColor(R.color.activated_tail))

        checkedTails[row][cell] = true
    }

    fun findPathAndMoveCat() {
        moveCat(findPath())
    }

    fun findPath(): View? {
        var row = currentPosition[0] + 1
        var cell = currentPosition[1] + 1

        // todo find path and check last cell for end game

        currentPosition[0] = row
        currentPosition[1] = cell

        return tails[row][cell]
    }

    fun moveCat(tail: View?) {
        if (tail != null) {
            val location = IntArray(2)
            tail.getLocationOnScreen(location)
            val offsetX = (tail.width - cat.width).toFloat() / 2
            val offsetY = (tail.height - cat.height).toFloat() / 2
            cat.x = location[0].toFloat() + offsetX
            cat.y = location[1].toFloat() + offsetY - statusBarHeight

            // todo add animation
        }
    }

}
