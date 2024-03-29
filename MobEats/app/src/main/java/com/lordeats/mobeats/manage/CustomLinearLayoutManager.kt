package com.lordeats.mobeats.manage

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.DisplayMetrics
import android.graphics.PointF
import androidx.recyclerview.widget.LinearSmoothScroller

class CustomLinearLayoutManager : LinearLayoutManager {

    constructor(context : Context) : super(context)

    constructor(context : Context, orientation : Int, reverseLayout : Boolean) : super(context, orientation, reverseLayout)

    constructor(context : Context, attrs : AttributeSet, defStyleAttr : Int, defStyleRes : Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        //super.smoothScrollToPosition(recyclerView, state, position)
        Log.d(CustomLinearLayoutManager::class.java.simpleName, "smoothScrollToPosition()")
        val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            private val MILLISECONDS_PER_INCH = 500f
            private val DISTANCE_IN_PIXELS = 500f
            private val DURATION = 500f

            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@CustomLinearLayoutManager
                    .computeScrollVectorForPosition(targetPosition)
            }

            override fun calculateTimeForScrolling(dx: Int): Int {
                val proportion = dx.toFloat() / DISTANCE_IN_PIXELS
                return (DURATION * proportion).toInt()
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                //return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                return super.calculateSpeedPerPixel(displayMetrics)
            }

        }

        linearSmoothScroller.computeScrollVectorForPosition(position)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}