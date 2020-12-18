package com.jerry.rbtreedemo.rbtree

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.ArrayDeque
import java.util.jar.Attributes

class RBTreeView : View {
    var rbTree: RBTree<*>? = null
        set(value) {
            field = value?.clone()
            invalidate()
        }

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val curFloorNode = ArrayDeque<Node<*>>()
    private val nextFloorNode = ArrayDeque<Node<*>>()
    private val nodePosQueue = ArrayDeque<Float>()

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : super(context,
        attributeSet,
        defStyleAttr)

    init {
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 50.0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val root = rbTree?.root ?: return

        curFloorNode.offer(root)
        var floorCount = 1
        var columnCount = 1 shl floorCount
        nodePosQueue.offer(1.0f / columnCount)

        do {
            while (curFloorNode.isNotEmpty()) {
                val node = curFloorNode.poll() ?: continue
                val posX = nodePosQueue.poll() ?: continue
                paint.color = if (node.isRed) Color.RED else Color.BLACK
                canvas.drawText(node.value.toString(), posX * width, floorCount * 100.0f, paint)
                if (node.left != null) {
                    nextFloorNode.offer(node.left)
                    nodePosQueue.offer(posX - 1.0f / (2 * columnCount))
                }
                if (node.right != null) {
                    nextFloorNode.offer(node.right)
                    nodePosQueue.offer(posX + 1.0f / (2 * columnCount))
                }
            }
            floorCount++
            columnCount = columnCount shl 1
            curFloorNode.addAll(nextFloorNode)
            nextFloorNode.clear()
        } while (curFloorNode.isNotEmpty())
    }
}