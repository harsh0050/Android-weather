package com.weather.forecast.clearsky.utils

import android.content.Context
import android.util.TypedValue
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlin.math.abs

class CustomOnTouchListener(
    context: Context,
    private val viewPager: ViewPager2,
    private val reloadingTextView: TextView,
    private val tabLayout: TabLayout,
    private val listener: OnRefreshCallback
) :
    OnGestureListener, OnTouchListener {

    private var gestureDetector: GestureDetector = GestureDetector(context, this)
    private var isVerticalScroll = false

    private val dpToPx: Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
    private val scrollFactor = 0.1f
    private val invisibleTransYDp = -10
    private val visibleTransYDp = 0

    private val invisibleTransYPx = invisibleTransYDp * dpToPx
    private val visibleTransYPx = visibleTransYDp * dpToPx
    var isReloading = false

    private var flag = false
    override fun onTouch(p0: View, motionEvent: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(motionEvent)) {
            //nothing
        } else if (motionEvent.action == MotionEvent.ACTION_UP) {
            viewPager.endFakeDrag()

            val transAnim = SpringAnimation(
                reloadingTextView,
                DynamicAnimation.TRANSLATION_Y,
                invisibleTransYPx
            ).apply {
                spring.stiffness = SpringForce.STIFFNESS_LOW
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }.addUpdateListener { _, value, _ ->
                val alpha = convertToAlphaRatio(value)
                reloadingTextView.alpha = alpha
                tabLayout.alpha = 1 - alpha
            }
            if(!isReloading){
                if(reloadingTextView.translationY==visibleTransYPx){
                    isReloading = true
                    listener.refresh(transAnim)
                }else{
                    transAnim.start()
                }
            }
        }
        return true
    }

    override fun onDown(p0: MotionEvent): Boolean {
        isVerticalScroll = false
        flag = true
        if(!isReloading)
            reloadingTextView.translationY = invisibleTransYPx
        viewPager.beginFakeDrag()
        return false
    }


    override fun onScroll(
        p0: MotionEvent?,
        p1: MotionEvent,
        distX: Float,
        distY: Float,
    ): Boolean {
        if (flag) {
            isVerticalScroll = abs(distX) < abs(distY)
        }
        flag = false
        if (isReloading || !isVerticalScroll)
            viewPager.fakeDragBy(-distX)
        else {
            if (reloadingTextView.translationY in invisibleTransYPx..visibleTransYPx) {
                reloadingTextView.let {
                    it.translationY = finalTranslation(it.translationY, distY)
                    val alpha = convertToAlphaRatio(it.translationY)
                    it.alpha = alpha
                    tabLayout.alpha = 1 - alpha
                }
            }
        }
        return false
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


    private fun finalTranslation(current: Float, distY: Float): Float {
        val final = current + (-distY * scrollFactor)
        return if (final >= visibleTransYPx)
            visibleTransYPx
        else if (final <= invisibleTransYPx)
            invisibleTransYPx
        else
            final
    }

    private fun convertToAlphaRatio(transY: Float): Float {
        val max = visibleTransYPx + (-invisibleTransYPx)
        val curr = transY + (-invisibleTransYPx)
        val alpha = curr / max
        return if (alpha <= 0)
            0f
        else if (alpha >= 1)
            1f
        else
            alpha
    }
}
interface OnRefreshCallback {
    fun refresh(springAnimation: SpringAnimation)
}