package com.sports.sportsflashes.view.customviewimpl

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import java.lang.ref.WeakReference


class CustomRecycler @JvmOverloads constructor(
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
}