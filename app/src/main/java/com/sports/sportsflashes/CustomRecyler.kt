package com.sports.sportsflashes

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import me.khrystal.library.widget.ItemViewMode
import java.lang.ref.WeakReference


class CustomRecyler @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context!!, attrs, defStyle) {
    private val mCenterRunnable: CenterRunnable =
        CenterRunnable()
    private var mIsForceCentering = false
    var mNeedCenterForce = false

    inner class CenterRunnable : Runnable {
        private var mView: WeakReference<View>? = null
        fun setView(v: View) {
            mView = WeakReference(v)
        }

        override fun run() {
            smoothScrollToView(mView!!.get()!!)
            if (mNeedCenterForce) mIsForceCentering = true
        }
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        removeCallbacks(mCenterRunnable)
        mIsForceCentering = false
        return super.onTouchEvent(e)
    }

    private var mCurrentCenterChildView: View? = null
    var mViewMode: ItemViewMode? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mViewMode != null) {
            val count = childCount
            for (i in 0 until count) {
                val v = getChildAt(i)
                mViewMode!!.applyToView(v, this)
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE) {
            mCurrentCenterChildView = findViewAtCenter()
            mCenterRunnable.setView(mCurrentCenterChildView!!)
            ViewCompat.postOnAnimation(this, mCenterRunnable)
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        if (mViewMode != null && layoutManager != null) {
            val count = layoutManager!!.childCount
            for (i in 0 until count) {
                val v = getChildAt(i)
                mViewMode!!.applyToView(v, this)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        //            scrollToPosition(DEFAULT_SELECTION);
      /*  val layoutManager = layoutManager as LoopingLayoutManager?
        if (layoutManager!!.canScrollHorizontally()) setPadding(
            width / 2,
            0,
            width / 2,
            0
        ) else if (layoutManager.canScrollVertically()) setPadding(
            0,
            height / 2,
            0,
            height / 2
        )
        clipToPadding = false
        clipChildren = false*/
        mCurrentCenterChildView = findViewAtCenter()
        smoothScrollToView(mCurrentCenterChildView!!)
    }

    fun findViewAt(x: Int, y: Int): View? {
        val count = childCount
        for (i in 0 until count) {
            val v = getChildAt(i)
            val x0 = v.left
            val y0 = v.top
            val x1 = v.width + x0
            val y1 = v.height + y0
            if (x >= x0 && x <= x1 && y >= y0 && y <= y1) {
                return v
            }
        }
        return null
    }

    fun findViewAtCenter(): View? {
        if (layoutManager!!.canScrollVertically()) {
            return findViewAt(0, height / 2)
        } else if (layoutManager!!.canScrollHorizontally()) {
            return findViewAt(width / 2, 0)
        }
        return null
    }

   fun smoothScrollToView(v: View) {
        var distance = 0
        if (layoutManager is LoopingLayoutManager) {
            if (layoutManager!!.canScrollVertically()) {
                val y = v.y + v.height * 0.5f
                val halfHeight = height * 0.5f
                distance = (y - halfHeight).toInt()
            } else if (layoutManager!!.canScrollHorizontally()) {
                val x = v.x + v.width * 0.5f
                val halfWidth = width * 0.5f
                distance = (x - halfWidth).toInt()
            }
        } else throw IllegalArgumentException("CircleRecyclerView just support T extend LinearLayoutManager!")
        smoothScrollBy(distance, distance)
    }

    /*override fun fling(velocityX: Int, velocityY: Int): Boolean {
        val linearLayoutManager = layoutManager as LoopingLayoutManager?
        val screenWidth: Int = Resources.getSystem().getDisplayMetrics().widthPixels

        // views on the screen

        // views on the screen
        val lastVisibleItemPosition = linearLayoutManager!!.topLeftIndex
        val lastView =
            linearLayoutManager!!.findViewByPosition(lastVisibleItemPosition)
        val firstVisibleItemPosition = linearLayoutManager!!.bottomRightIndex
        val firstView =
            linearLayoutManager!!.findViewByPosition(firstVisibleItemPosition)

        // distance we need to scroll

        // distance we need to scroll
        val leftMargin = (screenWidth - lastView!!.width) / 2
        val rightMargin = (screenWidth - firstView!!.width) / 2 + firstView!!.width
        val leftEdge = lastView!!.left
        val rightEdge = firstView!!.right
        val scrollDistanceLeft = leftEdge - leftMargin
        val scrollDistanceRight = rightMargin - rightEdge

        return if (Math.abs(velocityX) < 1000) {
            // The fling is slow -> stay at the current page if we are less than half through,
            // or go to the next page if more than half through
            if (leftEdge > screenWidth / 2) {
                // go to next page
                smoothScrollBy(-scrollDistanceRight, 0)
            } else if (rightEdge < screenWidth / 2) {
                // go to next page
                smoothScrollBy(scrollDistanceLeft, 0)
            } else {
                // stay at current page
                if (velocityX > 0) {
                    smoothScrollBy(-scrollDistanceRight, 0)
                } else {
                    smoothScrollBy(scrollDistanceLeft, 0)
                }
            }
            true
        } else {
            // The fling is fast -> go to next page
            if (velocityX > 0) {
                smoothScrollBy(scrollDistanceLeft, 0)
            } else {
                smoothScrollBy(-scrollDistanceRight, 0)
            }
            true
        }
    }*/


}