package com.kevin.viewtype.decoration

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ViewTypeDividerDecoration
 *
 * @author zwenkai@foxmail.com, Created on 2020-07-08 23:03:21
 *         Major Function：<b>ViewTypeDividerDecoration</b>
 *         <p/>
 *         Note: If you modify this class please fill in the following content as a record.
 * @author mender，Modified Date Modify Content:
 */

class ViewTypeDividerDecoration private constructor(private val builder: Builder) : ViewTypeSpaceDecoration(builder) {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val targetView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(targetView)
            if (position == RecyclerView.NO_POSITION) continue
            drawChildDivider(c, parent, targetView, position)
        }
    }

    private fun drawChildDivider(c: Canvas, parent: RecyclerView, targetView: View, position: Int) {
        val viewType = parent.adapter!!.getItemViewType(position)
        val isFirst = position == 0
        val isLast = position == parent.childCount - 1

        // 默认不绘制第一个条目头部
        if (isFirst) return

        val spaces = targetView.getTag(R.id.tag_view_type_space_decoration) as IntArray? ?: return

        c.drawRect(
            parent.paddingLeft + builder.paddingStart + spaces[0].toFloat(),
            targetView.top - spaces[1].toFloat(),
            parent.width - parent.paddingRight - builder.paddingEnd - spaces[2].toFloat(),
            targetView.top.toFloat(),
            builder.defaultPaint
        )
    }

    class Builder : ViewTypeSpaceDecoration.Builder() {
        val defaultPaint = Paint()
        var paddingStart = 0
        var paddingEnd = 0

        /**
         * 默认分割线颜色
         */
        fun defaultColor(color: Int): Builder {
            defaultPaint.isAntiAlias = true
            defaultPaint.color = color
            return this
        }

        override fun defaultSpace(spacePx: Int): Builder {
            return defaultSpace(spacePx, 0, 0)
        }

        fun defaultSpace(spacePx: Int, paddingStart: Int = 0, paddingEnd: Int = 0): Builder {
            super.defaultSpace(spacePx)
            this.paddingStart = paddingStart
            this.paddingEnd = paddingEnd
            return this
        }

        override fun defaultSideSpace(spacePx: Int): Builder {
            super.defaultSideSpace(spacePx)
            return this
        }

        override fun build(): ViewTypeDividerDecoration {
            return ViewTypeDividerDecoration(this)
        }
    }
}