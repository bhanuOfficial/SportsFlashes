package com.sports.sportsflashes

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class LooperLayoutManager : RecyclerView.LayoutManager() {
     var looperEnable: Boolean=false

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        if (itemCount <= 0) {
            return
        }
        // Note 1. If the current state of readiness, return directly
        if (state.isPreLayout) {
            return
        }
        // Note 2. Separate views into scrap caches in preparation for re-typesetting views
        detachAndScrapAttachedViews(recycler)
        var autualWidth = 0
        for (i in 0 until itemCount) {
            // Annotation 3. Initialization, filling in the view in the screen
            val itemView: View = recycler.getViewForPosition(i)
            addView(itemView)
            // Note 4. Measuring the width and height of itemView
            measureChildWithMargins(itemView, 0, 0)
            val width = getDecoratedMeasuredWidth(itemView)
            val height = getDecoratedMeasuredHeight(itemView)
            // Annotation 5. Layout according to the width of itemView
            layoutDecorated(itemView, autualWidth, 0, autualWidth + width, height)
            autualWidth += width
            // Note 6. If the total width of the itemView currently laid out is larger than that of RecyclerView, the layout is no longer done.
            if (autualWidth > getWidth()) {
                break
            }
        }
    }
    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: Recycler?,
        state: RecyclerView.State?
    ): Int {
        // Note 1. Fill itemView in order on both left and right sides when sliding horizontally
        val travl: Int = fill(dx, recycler!!, state!!)
        if (travl == 0) {
            return 0
        }

        // 2. Sliding
        offsetChildrenHorizontal(-travl)

        // 3. Recycling the invisible itemView
        recyclerHideView(dx, recycler, state)
        return travl
    }

    private fun fill(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        var dx = dx
        if (dx > 0) {
            // Note 1. Scroll left
            val lastView = getChildAt(childCount - 1) ?: return 0
            val lastPos = getPosition(lastView)
            // Note 2. The last itemView visible has completely slipped in and needs to be added.
            if (lastView.right < width) {
                var scrap: View? = null
                // Note 3. Judge the index of the last itemView visible,
                // If it's the last, set the next itemView to the first, or set it to the next of the current index
                if (lastPos == itemCount - 1) {
                    if (looperEnable) {
                        scrap = recycler.getViewForPosition(0)
                    } else {
                        dx = 0
                    }
                } else {
                    scrap = recycler.getViewForPosition(lastPos + 1)
                }
                if (scrap == null) {
                    return dx
                }
                // Note 4. Enter the new itemViewadd and measure and layout it
                addView(scrap)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(
                    scrap, lastView.right, 0,
                    lastView.right + width, height
                )
                return dx
            }
        } else {
            // Scroll to the right
            val firstView = getChildAt(0) ?: return 0
            val firstPos = getPosition(firstView)
            if (firstView.left >= 0) {
                var scrap: View? = null
                if (firstPos == 0) {
                    if (looperEnable) {
                        scrap = recycler.getViewForPosition(itemCount - 1)
                    } else {
                        dx = 0
                    }
                } else {
                    scrap = recycler.getViewForPosition(firstPos - 1)
                }
                if (scrap == null) {
                    return 0
                }
                addView(scrap, 0)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(
                    scrap, firstView.left - width, 0,
                    firstView.left, height
                )
            }
        }
        return dx
    }

    private fun recyclerHideView(
        dx: Int,
        recycler: Recycler,
        state: RecyclerView.State
    ) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            if (dx > 0) {
                // Note 1. Scroll left to remove views that are not in the content on the left.
                if (view.right < 0) {
                    removeAndRecycleView(view, recycler)
                    Log.d("BHANU", "Cycle: Remove a view child Count=$childCount")
                }
            } else {
                // Note 2. Scroll to the right to remove views that are not in the content on the right.
                if (view.left > width) {
                    removeAndRecycleView(view, recycler)
                    Log.d("BHANU", "Cycle: Remove a view child Count=$childCount")
                }
            }
        }
    }


}