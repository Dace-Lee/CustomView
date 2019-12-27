package com.dace.customview.card

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.MotionEvent
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener

class CardLayoutManager() : RecyclerView.LayoutManager() {
    private var mRecyclerView: RecyclerView? = null
    private var mItemTouchHelper: ItemTouchHelper? = null
    private val mOnTouchListener = OnTouchListener { v, event ->
        val childViewHolder = mRecyclerView?.getChildViewHolder(v)
        // 把触摸事件交给 mItemTouchHelper，让其处理卡片滑动事件
        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
            if (childViewHolder != null) {
                mItemTouchHelper?.startSwipe(childViewHolder)
            }
        }
        false
    }

    constructor(recyclerView: RecyclerView, itemTouchHelper: ItemTouchHelper) : this() {
        this.mRecyclerView = checkIsNull(recyclerView)
        this.mItemTouchHelper = checkIsNull(itemTouchHelper)
    }

    private fun <T> checkIsNull(t: T?): T {
        if (t == null) {
            throw NullPointerException()
        }
        return t
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (itemCount <= 0 || state!!.isPreLayout) {
            removeAndRecycleAllViews(recycler!!)
            return
        }
        detachAndScrapAttachedViews(recycler!!)
        for (index in itemCount - 1 downTo 0) {
            val view = recycler.getViewForPosition(index)
            if (index > CardConfig.DEFAULT_SHOW_ITEM) {
                removeAndRecycleView(view, recycler)
            } else {
                fill(view)
                when {
                    index == CardConfig.DEFAULT_SHOW_ITEM -> {
                        view.scaleX = 1 - (index - 1) * CardConfig.DEFAULT_SCALE
                        view.scaleY = 1 - (index - 1) * CardConfig.DEFAULT_SCALE
                        view.translationY = ((index - 1) * view.measuredHeight / CardConfig.DEFAULT_TRANSLATE_Y).toFloat()
                    }
                    index > 0 -> {
                        view.scaleX = 1 - index * CardConfig.DEFAULT_SCALE
                        view.scaleY = 1 - index * CardConfig.DEFAULT_SCALE
                        view.translationY = (index * view.measuredHeight / CardConfig.DEFAULT_TRANSLATE_Y).toFloat()
                    }
                    else -> {
                        view.setOnTouchListener(mOnTouchListener)
                    }
                }
            }
        }
    }

    private fun fill(view: View) {
        addView(view)
        measureChildWithMargins(view, 0, 0)
        val decoratedMeasuredWidth = getDecoratedMeasuredWidth(view)
        val widthSpace = width - decoratedMeasuredWidth
        val decoratedMeasuredHeight = getDecoratedMeasuredHeight(view)
        val heightSpace = height - decoratedMeasuredHeight
        layoutDecoratedWithMargins(
            view,
            widthSpace / 2,
            heightSpace / 2,
            widthSpace / 2 + decoratedMeasuredWidth,
            heightSpace / 2 + decoratedMeasuredHeight
        )
    }
}