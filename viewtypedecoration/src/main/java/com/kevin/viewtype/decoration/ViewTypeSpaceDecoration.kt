package com.kevin.viewtype.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewTypeSpaceDecoration
 *
 * @author zhouwenkai@foxmail.com, Created on 2020-07-08 22:45:08
 *         Major Function：<b>ViewTypeSpaceDecoration</b>
 *         <p/>
 *         Note: If you modify this class please fill in the following content as a record.
 * @author mender，Modified Date Modify Content:
 */

open class ViewTypeSpaceDecoration internal constructor(private val builder: Builder) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION || view.visibility == View.GONE || parent.adapter == null) {
            return
        }

        // 设置通用分割空间及首行分割线
        val isFirst = isFirstItem(view, parent)
        outRect.top = if (isFirst) builder.firstSpace else builder.defaultSpace
        // 设置通用边距空间
        outRect.left = builder.defaultSideSpace
        outRect.right = builder.defaultSideSpace

        val viewType = parent.adapter!!.getItemViewType(position)
        val previousType: Int =
            if (position > 0) parent.adapter!!.getItemViewType(position - 1) else RecyclerView.INVALID_TYPE

        // 左右边距设置
        builder.sideSpaces.firstOrNull { it.viewType == viewType }?.let { sideSpace ->
            when {
                isBothSideItem(view, parent) -> {
                    outRect.left = sideSpace.startSpacePx
                    outRect.right = sideSpace.endSpacePx
                }
                isLeftSideItem(view, parent) -> {
                    outRect.left = sideSpace.startSpacePx
                }
                isRightSideItem(view, parent) -> {
                    outRect.right = sideSpace.endSpacePx
                }
            }
        }

        builder.adjacentSpaces.firstOrNull { it.previousViewType == previousType && it.viewType == viewType }
            ?.let { adjacentSpace ->
                outRect.top = adjacentSpace.spacePx
            }

        // save spaces to View
        val savedSpaces = view.getTag(R.id.tag_view_type_space_decoration)
        val spaces = if (savedSpaces != null && savedSpaces is IntArray) savedSpaces else IntArray(4)
        spaces[0] = outRect.left
        spaces[1] = outRect.top
        spaces[2] = outRect.right
        spaces[3] = outRect.bottom
        view.setTag(R.id.tag_view_type_space_decoration, spaces)
    }

    open class Builder {
        var firstSpace = 0
        var defaultSpace = 0
        var defaultSideSpace = 0
        val sideSpaces = mutableListOf<SideSpace>()
        val adjacentSpaces = mutableListOf<Adjacent>()

        /**
         * 设置首行的间距
         */
        open fun firstSpace(spacePx: Int): Builder {
            this.firstSpace = spacePx
            return this
        }

        /**
         * 设置默认间距（默认第一行前和最后一行后 0 间距）
         */
        open fun defaultSpace(spacePx: Int): Builder {
            this.defaultSpace = spacePx
            return this
        }

        fun sideSpace(viewType: Int, spacePx: Int): Builder {
            sideSpaces.add(SideSpace(viewType, spacePx, spacePx))
            return this
        }

        fun sideSpace(viewType: Int, startSpacePx: Int, endSpacePx: Int): Builder {
            sideSpaces.add(SideSpace(viewType, startSpacePx, endSpacePx))
            return this
        }

        open fun defaultSideSpace(spacePx: Int): Builder {
            this.defaultSideSpace = spacePx
            return this
        }

        /**
         * 设置相邻两类型间的间距
         */
        fun adjacentSpace(previousType: Int, viewType: Int, spacePx: Int): Builder {
            adjacentSpaces.add(Adjacent(previousType, viewType, spacePx))
            return this
        }

        open fun build(): ViewTypeSpaceDecoration {
            return ViewTypeSpaceDecoration(this)
        }

    }

    /**
     * 判断是否是首行条目
     */
    private fun isFirstItem(view: View, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        val position = parent.getChildAdapterPosition(view)
        return if (layoutManager !is GridLayoutManager) {
            position == 0
        } else {
            // TODO 判断是否第一行有问题，需要优化
            position < layoutManager.spanCount
        }
    }

    /**
     * 判断是否跨度整行的条目
     *
     * @param view
     * @param parent
     */
    private fun isBothSideItem(view: View, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        return if (layoutManager !is GridLayoutManager) {
            true
        } else {
            val position = parent.getChildAdapterPosition(view)
            val positionSpanSize: Int = layoutManager.spanSizeLookup.getSpanSize(position)
            positionSpanSize == layoutManager.spanCount
        }
    }

    /**
     * 判断是否是最左侧条目
     *
     * @param view
     * @param parent
     */
    private fun isLeftSideItem(view: View, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        return if (layoutManager !is GridLayoutManager) {
            true
        } else {
            val position = parent.getChildAdapterPosition(view)
            val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position, layoutManager.spanCount)
            spanIndex == 0
        }
    }

    /**
     * 判断是否是最右侧条目
     *
     * @param view
     * @param parent
     */
    private fun isRightSideItem(view: View, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        return if (layoutManager !is GridLayoutManager) {
            true
        } else {
            val position = parent.getChildAdapterPosition(view)
            val spanIndex =
                layoutManager.spanSizeLookup.getSpanIndex(position, layoutManager.spanCount)
            val positionSpanSize: Int = layoutManager.spanSizeLookup.getSpanSize(position)
            spanIndex + positionSpanSize == layoutManager.spanCount
        }
    }

    /**
     * 左右边距实体类
     */
    class SideSpace(val viewType: Int, val startSpacePx: Int, val endSpacePx: Int)

    /**
     * 相邻两类型间距
     */
    class Adjacent(val previousViewType: Int, val viewType: Int, val spacePx: Int)
}