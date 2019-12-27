package com.dace.customview.card

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class ItemTouchHelperCallback<T>(
    private val dataList: MutableList<T>,
    private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    private var mListener: OnSwipeListener<T>?
) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = 0
        var swipeFlags = 0
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is CardLayoutManager) {
            swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 移除之前设置的 onTouchListener, 否则触摸滑动会乱了
        viewHolder.itemView.setOnTouchListener(null)
        // 删除相对应的数据

        val layoutPosition = viewHolder.layoutPosition
        val remove = dataList.removeAt(layoutPosition)
        adapter.notifyDataSetChanged()
        // 卡片滑出后回调 OnSwipeListener 监听器
        mListener?.onSwiped(
            viewHolder,
            remove,
            if (direction == ItemTouchHelper.LEFT) CardConfig.SWIPED_LEFT else CardConfig.SWIPED_RIGHT
        )

        // 当没有数据时回调 OnSwipeListener 监听器

        if (adapter.itemCount == 0) {
            mListener?.onSwipedClear()
        }
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            var ratio = dX / getThreshold(recyclerView, viewHolder)
            // ratio 最大为 1 或 -1
            if (ratio > 1) {
                ratio = 1f
            } else if (ratio < -1) {
                ratio = -1f
            }
            itemView.rotation = ratio * CardConfig.DEFAULT_ROTATE_DEGREE
            val childCount = recyclerView.childCount
            // 当数据源个数大于最大显示数时
            if (childCount > CardConfig.DEFAULT_SHOW_ITEM) {
                for (position in 1 until childCount - 1) {
                    val index = childCount - position - 1
                    val view = recyclerView.getChildAt(position)
                    view.scaleX = 1 - index * CardConfig.DEFAULT_SCALE + Math.abs(ratio) * CardConfig.DEFAULT_SCALE
                    view.scaleY = 1 - index * CardConfig.DEFAULT_SCALE + Math.abs(ratio) * CardConfig.DEFAULT_SCALE
                    view.translationY =
                        (index - Math.abs(ratio)) * itemView.measuredHeight / CardConfig.DEFAULT_TRANSLATE_Y
                }
            } else {
                // 当数据源个数小于或等于最大显示数时
                for (position in 0 until childCount - 1) {
                    val index = childCount - position - 1
                    val view = recyclerView.getChildAt(position)
                    view.scaleX = 1 - index * CardConfig.DEFAULT_SCALE + Math.abs(ratio) * CardConfig.DEFAULT_SCALE
                    view.scaleY = 1 - index * CardConfig.DEFAULT_SCALE + Math.abs(ratio) * CardConfig.DEFAULT_SCALE
                    view.translationY =
                        (index - Math.abs(ratio)) * itemView.measuredHeight / CardConfig.DEFAULT_TRANSLATE_Y
                }
            }
            if (ratio != 0f) {
                mListener?.onSwiping(
                    viewHolder,
                    ratio,
                    if (ratio < 0) CardConfig.SWIPING_LEFT else CardConfig.SWIPING_RIGHT
                )
            } else {
                mListener?.onSwiping(viewHolder, ratio, CardConfig.SWIPING_NONE)
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.rotation = 0f
    }

    private fun getThreshold(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Float {
        return recyclerView.width * getSwipeThreshold(viewHolder)
    }
}