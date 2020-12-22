package com.jerry.rbtreedemo.rbtree

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class RBTreeView : View {
    /**
     * UI树根节点
     */
    var rootNode: TreeViewNode<*>? = null
        set(value) {
            preRootNode = field
            field = value
            calTreePos()
            flatTree()
            startStepAnim()
        }

    /**
     * UI树发生变化前的根节点
     */
    private var preRootNode: TreeViewNode<*>? = null
    private var allNodeMap: MutableMap<Any, Array<TreeViewNode<*>?>> = HashMap()

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
     * 计算树的位置
     */
    private fun calTreePos() {
        calNodePos(rootNode, 1, 0f)
    }

    /**
     * 通过递归计算树上每个节点的位置
     *
     * @param node 节点
     * @param floorIndex 节点所在层
     * @param xOffset 节点相对于中心的偏移
     */
    private fun calNodePos(node: TreeViewNode<*>?, floorIndex: Int, xOffset: Float) {
        node ?: return
        node.pos = PointF(xOffset, floorIndex.toFloat())
        calNodePos(node.left, floorIndex.plus(1), xOffset.minus(1f / (1 shl floorIndex.plus(1))))
        calNodePos(node.right, floorIndex.plus(1), xOffset.plus(1f / (1 shl floorIndex.plus(1))))
    }

    /**
     * 把树拍平为集合
     */
    private fun flatTree() {
        allNodeMap.clear()
        // 这次变化前的树
        val preNodeSet = TreeHelper.flatTree(preRootNode)
        // 这次变化后的树
        val curNodeSet = TreeHelper.flatTree(rootNode)
        // 俩树合并
        preNodeSet.forEach {
            allNodeMap[it.value] = arrayOf(it, null)
        }
        curNodeSet.forEach {
            val nodeList = allNodeMap[it.value]
            if (nodeList != null) {
                nodeList[1] = it
            } else {
                allNodeMap[it.value] = arrayOf(null, it)
            }
        }
    }

    /**
     * 开始每一步的动画
     */
    private fun startStepAnim() {
        valueAnim.end()
        if (preRootNode == null) {
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
        drawTree(canvas, allNodeMap, animProgress)
    }

    /**
     * 绘制树
     *
     * @param canvas 画布
     * @param nodeMap 节点Map
     * @param animProgress 动画进度
     */
    private fun drawTree(
        canvas: Canvas,
        nodeMap: MutableMap<Any, Array<TreeViewNode<*>?>>,
        animProgress: Float
    ) {
        nodeMap.forEach {
            val preNode = it.value[0]
            val curNode = it.value[1]
            var childNode: TreeViewNode<*>? = null
            var parentNode: TreeViewNode<*>? = null
            if (preNode != null && curNode != null) {
                childNode = curNode.clone()
                childNode.pos = preNode.pos + (curNode.pos - preNode.pos) * animProgress
            }/* else if (preNode == null && curNode != null) {
                childNode = curNode.clone()
                childAlpha = animProgress
            } else if (preNode != null && curNode == null) {
                childNode = preNode.clone()
                childAlpha = 1 - animProgress
            }*/

            val preParentNode = preNode?.parent
            val curParentNode = curNode?.parent
            if (preParentNode != null && curParentNode != null) {
                if (preParentNode == curParentNode) {
                    parentNode = curParentNode.clone()
                    parentNode.pos =
                        preParentNode.pos + (curParentNode.pos - preParentNode.pos) * animProgress
                }
            }/* else if (preParentNode == null && curParentNode != null) {
                parentNode = curParentNode.clone()
                parentAlpha = animProgress
            } else if (preParentNode != null && curParentNode == null) {
                parentNode = preParentNode.clone()
                parentAlpha = 1 - animProgress
            }*/

            childNode ?: return
            drawBranch(canvas, childNode, parentNode)
        }
    }

    /**
     * 绘制树枝（子节点和父节点相连）
     *
     * @param canvas 画布
     * @param childNode 子节点
     * @param parentNode 父节点
     */
    private fun drawBranch(
        canvas: Canvas,
        childNode: TreeViewNode<*>,
        parentNode: TreeViewNode<*>?
    ) {
        if (parentNode != null) {
            // 如果有父节点先画线
            drawLine(canvas, childNode, parentNode)
            // 画父节点
            drawNode(canvas, parentNode)
        }

        // 画子节点
        drawNode(canvas, childNode)
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
        childNode: TreeViewNode<*>,
        parentNode: TreeViewNode<*>
    ) {
        paint.color = Color.BLUE
        canvas.drawLine(childNode.pos.x.times(width),
            childNode.pos.y.times(floorHeight),
            parentNode.pos.x.times(width),
            parentNode.pos.y.times(floorHeight),
            paint)
    }

    /**
     * 绘制节点
     *
     * @param canvas 画布
     * @param node 节点
     */
    private fun drawNode(canvas: Canvas, node: TreeViewNode<*>) {
        // 画节点圆
        paint.color = if (node.isRed) Color.RED else Color.BLACK
        canvas.drawCircle(node.pos.x.times(width), node.pos.y.times(floorHeight), nodeRadius, paint)
        // 画节点字
        paint.color = Color.WHITE
        val yPos = node.pos.y.times(floorHeight) - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(node.value.toString(), node.pos.x.times(width), yPos, paint)
    }

    /**
     * UI树节点
     */
    data class TreeViewNode<T : Comparable<T>>(
        var pos: PointF = PointF(0f, 0f),
        val value: T,
        val isRed: Boolean,
        override var parent: TreeViewNode<T>? = null,
        override var left: TreeViewNode<T>? = null,
        override var right: TreeViewNode<T>? = null
    ) : ITreeNode<TreeViewNode<T>>, Cloneable {
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