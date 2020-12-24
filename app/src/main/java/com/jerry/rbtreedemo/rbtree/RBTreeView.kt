package com.jerry.rbtreedemo.rbtree

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.View

private const val TAG = "RBTreeView"

class RBTreeView<V : Comparable<V>> : View {
    /**
     * UI树根节点
     */
    var rootNode: TreeViewNode<V>? = null
        set(value) {
            preNodeMap = TreeHelper.flatTree(field)
            Log.i(TAG, "preNodeMap: $preNodeMap")
            field = value
            startStepAnim()
        }

    /**
     * 变化前节点Map
     * （key = 节点value，
     * value = 节点对象）
     */
    private var preNodeMap = HashMap<V, TreeViewNode<V>>()

    private val valueAnim = ValueAnimator.ofFloat(0f, 1f)
    private var animProgress: Float = 0f

    /**
     * 树的每层高
     */
    var floorHeight = 100f

    /**
     * 节点圆大小
     */
    var nodeRadius = 45f

    /**
     * 画笔
     */
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : super(context,
        attributeSet,
        defStyleAttr)

    init {
        paint.strokeWidth = 10f
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 50f

        valueAnim.duration = 1000
        valueAnim.addUpdateListener {
            animProgress = it.animatedFraction
            invalidate()
        }
    }

    /**
     * 开始每一步的动画
     */
    private fun startStepAnim() {
        valueAnim.end()
        if (preNodeMap.isEmpty()) {
            invalidate()
        } else {
            valueAnim.start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        rootNode ?: return
        // 移动画布到中间位置
        canvas.translate(width / 2f, 0f)
        drawTree(canvas, rootNode, 1, 0f)
    }

    /**
     * 绘制树
     *
     * @param canvas 画布
     * @param node 节点
     * @param floorIndex 层数
     * @param xOffset 水平偏移
     */
    private fun drawTree(
        canvas: Canvas,
        node: TreeViewNode<V>?,
        floorIndex: Int,
        xOffset: Float
    ) {
        node ?: return

        // 计算节点位置
        node.pos.x = xOffset * width / 2f
        node.pos.y = floorIndex * floorHeight

        drawTree(canvas, node.left, floorIndex + 1, xOffset - (1f / (1 shl floorIndex)))
        drawTree(canvas, node.right, floorIndex + 1, xOffset + (1f / (1 shl floorIndex)))

        // 所有节点位置计算完成之后再绘制，因为画线需要知道父节点的位置并且线要绘在最下层
        val parentNode = node.parent
        if (parentNode != null) {
            // 如果有父节点则先画线
            drawLine(canvas, node, parentNode)
        }

        // 画节点
        drawNode(canvas, node)
    }

    /**
     * 绘制节点间连线
     *
     * @param canvas 画布
     * @param childNode 子节点
     * @param parentNode 父节点
     */
    private fun drawLine(
        canvas: Canvas,
        childNode: TreeViewNode<V>,
        parentNode: TreeViewNode<V>
    ) {
        val childRealPos: PointF
        val parentRealPos: PointF
        val preChildNode = preNodeMap[childNode.value]
        val preParentNode = preNodeMap[parentNode.value]
        childRealPos = if (preChildNode != null) {
            preChildNode.pos + (childNode.pos - preChildNode.pos) * animProgress
        } else {
            childNode.pos
        }
        parentRealPos = if (preParentNode != null) {
            preParentNode.pos + (parentNode.pos - preParentNode.pos) * animProgress
        } else {
            parentNode.pos
        }

        paint.color = Color.BLUE
        canvas.drawLine(childRealPos.x,
            childRealPos.y,
            parentRealPos.x,
            parentRealPos.y,
            paint)
    }

    /**
     * 绘制节点
     *
     * @param canvas 画布
     * @param node 节点
     */
    private fun drawNode(canvas: Canvas, node: TreeViewNode<V>) {
        val nodeRealPos: PointF
        val preNode = preNodeMap[node.value]
        nodeRealPos = if (preNode != null) {
            preNode.pos + (node.pos - preNode.pos) * animProgress
        } else {
            node.pos
        }
        // 画节点圆
        paint.color = if (node.isRed) Color.RED else Color.BLACK
        canvas.drawCircle(nodeRealPos.x, nodeRealPos.y, nodeRadius, paint)
        // 画节点字
        paint.color = Color.WHITE
        val yPos = nodeRealPos.y - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(node.value.toString(), nodeRealPos.x, yPos, paint)
    }

    /**
     * UI树节点
     */
    data class TreeViewNode<T : Comparable<T>>(
        var pos: PointF = PointF(0f, 0f),
        override val value: T,
        val isRed: Boolean,
        override var parent: TreeViewNode<T>? = null,
        override var left: TreeViewNode<T>? = null,
        override var right: TreeViewNode<T>? = null
    ) : ITreeNode<TreeViewNode<T>, T>, Cloneable {
        override fun compareTo(other: TreeViewNode<T>): Int {
            return value.compareTo(other.value)
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is TreeViewNode<*>) value == other.value else false
        }

        public override fun clone(): TreeViewNode<T> {
            return TreeViewNode(PointF(pos), value, isRed)
        }

        override fun toString(): String {
            return "{pos = $pos, value = $value, isRed = $isRed}"
        }
    }

    operator fun PointF.minus(p: PointF): PointF {
        return PointF(x - p.x, y - p.y)
    }

    operator fun PointF.plus(p: PointF): PointF {
        return PointF(x + p.x, y + p.y)
    }

    operator fun PointF.times(t: Float): PointF {
        return PointF(x * t, y * t)
    }
}