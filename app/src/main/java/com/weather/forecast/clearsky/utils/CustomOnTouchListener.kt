package com.weather.forecast.clearsky.utils

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.viewpager2.widget.ViewPager2

class CustomOnTouchListener(context: Context, private val viewPager: ViewPager2) :
    OnGestureListener, OnTouchListener {

    private var gestureDetector: GestureDetector = GestureDetector(context, this)
    override fun onTouch(p0: View, motionEvent: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(motionEvent)) {
            true
        } else if (motionEvent.action == MotionEvent.ACTION_UP) {
            viewPager.endFakeDrag()
            true
        } else {
            false
        }
    }

    override fun onDown(p0: MotionEvent): Boolean {
        viewPager.beginFakeDrag()
        return true
    }


    override fun onScroll(
        p0: MotionEvent?,
        p1: MotionEvent,
        distX: Float,
        distY: Float,
    ): Boolean {
        viewPager.fakeDragBy(-distX)
        return true
    }

    override fun onFling(
        p0: MotionEvent?,
        p1: MotionEvent,
        velX: Float,
        velY: Float,
    ): Boolean = false

    override fun onShowPress(p0: MotionEvent) {}

    override fun onLongPress(p0: MotionEvent) {}

    override fun onSingleTapUp(p0: MotionEvent): Boolean = false

}