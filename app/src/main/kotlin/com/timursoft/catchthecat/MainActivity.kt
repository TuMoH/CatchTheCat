package com.timursoft.catchthecat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        frameLayout {
            lparams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }

            var cat = imageView {
                lparams {
                    width = ViewGroup.LayoutParams.WRAP_CONTENT
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                imageResource = R.drawable.ic_insert_emoticon_black_24dp
            }

            verticalLayout {
                lparams {
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = ViewGroup.LayoutParams.MATCH_PARENT
                }
                gravity = Gravity.CENTER

                for (i in 1..11) {
                    linearLayout {
                        lparams {
                            width = ViewGroup.LayoutParams.MATCH_PARENT
                            height = ViewGroup.LayoutParams.WRAP_CONTENT
                            if (i % 2 == 0) {
                                leftMargin = dip(10)
                            } else {
                                rightMargin = dip(10)
                            }
                        }

                        for (j in 1..11) {
                            imageView {
                                lparams {
                                    weight = 1f
                                    width = ViewGroup.LayoutParams.MATCH_PARENT
                                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                                }
                                adjustViewBounds = true
                                imageResource = R.drawable.ic_lens_black_24dp

                                onClick {
                                    setColorFilter(resources.getColor(R.color.activated_tail))
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}
