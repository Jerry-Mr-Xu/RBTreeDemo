package com.jerry.rbtreedemo.rbtree

import java.lang.Exception
import java.lang.NullPointerException
import java.util.ArrayDeque

private const val TAG = "RBTree"

class RBTree<T : Comparable<T>> : Cloneable {
    var root: TreeNode<T>? = null
    var size = 0

    var onChangeListener: OnNodeChangeListener? = null

    /**
     * 插入节点
     *
     * @param value 节点的值
     * @throws RepeatNodeException 重复节点异常
     */
    @Throws(RepeatNodeException::class, RotateNodeException::class, NullPointerException::class)
    fun addNode(value: T) {
        onChangeListener?.startChange()
        if (root == null) {
            root = TreeNode(value)
            onChangeListener?.onChange()
            size++
            onChangeListener?.endChange()
            return
        }

        val parent = findParentNode(value) ?: throw NullPointerException("没有父节点")
        if (parent.value == value) {
            onChangeListener?.endChange()
            // 如果和父节点的值重复则抛出异常
            throw RepeatNodeException("树中有相同值的节点")
        }

        val newNode = TreeNode(value, true, parent)
        if (newNode < parent) {
            parent.left = newNode
        } else {
            parent.right = newNode
        }
        onChangeListener?.onChange()

        fixInsert(newNode)
        if (root!!.isRed) {
            root!!.isRed = false
            onChangeListener?.onChange()
        }
        size++
        onChangeListener?.endChange()
    }

    /**
     * 移除节点
     *
     * @param node 要移除的节点
     */
    fun removeNode(node: TreeNode<T>) {
        removeNode(node.value)
    }

    /**
     * 移除节点
     *
     * @param value 要移除节点的值
     */
    fun removeNode(value: T) {
        onChangeListener?.startChange()
        if (root == null) {
            // 如果没有根节点，结束
            onChangeListener?.endChange()
            return
        }

        val parent = findParentNode(value)
        if (parent == null || parent.value != value) {
            // 如果没有找到对应的节点，结束
            onChangeListener?.endChange()
            return
        }
        val node = parent
    }

    /**
     * 插入修复
     * 通过旋转变色等方式修复为满足红黑树规则
     *
     * @param insertNode 插入的节点
     */
    private fun fixInsert(insertNode: TreeNode<T>) {
        var node = insertNode
        var parent = node.parent

        while (parent != null && parent.isRed) {
            val uncle = getUncle(node)
            if (uncle == null || !uncle.isRed) {
                // 如果叔叔为空，或叔叔为黑色
                if (parent == parent.parent?.left) {
                    if (node == parent.right) {
                        /**
                         *    祖
                         * 父
                         *    子
                         * 先对子节点左旋
                         *       祖
                         *    子
                         * 父
                         * 再对子节点右旋
                         *    子
                         * 父     祖
                         */
                        rotateLeft(node)
                        onChangeListener?.onChange()
                        rotateRight(node)
                        onChangeListener?.onChange()
                        // 把子变黑，把祖变红
                        node.isRed = false
                        node.right?.isRed = true
                        onChangeListener?.onChange()
                    } else if (node == parent.left) {
                        /**
                         *       祖
                         *    父
                         * 子
                         * 对父节点右旋
                         *    父
                         * 子    祖
                         */
                        rotateRight(parent)
                        onChangeListener?.onChange()
                        // 把父变黑，把祖变红
                        parent.isRed = false
                        parent.right?.isRed = true
                        onChangeListener?.onChange()
                    }
                } else if (parent == parent.parent?.right) {
                    if (node == parent.left) {
                        /**
                         * 祖
                         *    父
                         * 子
                         * 先对子节点右旋
                         * 祖
                         *    子
                         *       父
                         * 再对子节点左旋
                         *    子
                         * 祖    父
                         */
                        rotateRight(node)
                        onChangeListener?.onChange()
                        rotateLeft(node)
                        onChangeListener?.onChange()
                        // 把子变黑，把祖变红
                        node.isRed = false
                        node.left?.isRed = true
                        onChangeListener?.onChange()
                    } else if (node == parent.right) {
                        /**
                         * 祖
                         *    父
                         *       子
                         * 对父节点左旋
                         *    父
                         * 祖    子
                         */
                        rotateLeft(parent)
                        onChangeListener?.onChange()
                        // 把父变黑，把祖变红
                        parent.isRed = false
                        parent.left?.isRed = true
                        onChangeListener?.onChange()
                    }
                }
                parent = null
            } else {
                parent.isRed = false
                uncle.isRed = false
                parent.parent?.isRed = true
                onChangeListener?.onChange()
                node = parent.parent!!
                parent = node.parent
            }
        }
    }

    /**
     * 左旋操作
     *
     * @param node 要左旋的节点
     */
    private fun rotateLeft(node: TreeNode<T>) {
        val parent = node.parent ?: throw NullPointerException("没有父节点")
        val ancestor = parent.parent

        // 如果要左旋的节点不是父节点的右子节点，则无效左旋
        if (node != parent.right) throw RotateNodeException("要左旋的节点不是父节点的右子节点")

        // 父节点变为要左旋节点的左子节点
        parent.right = node.left
        node.left?.parent = parent
        parent.parent = node
        node.left = parent
        node.parent = ancestor

        // 要左旋节点代替原父节点
        when {
            ancestor?.left == parent -> {
                ancestor.left = node
            }
            ancestor?.right == parent -> {
                ancestor.right = node
            }
            ancestor == null -> {
                root = node
            }
        }
    }

    /**
     * 右旋操作
     *
     * @param node 要右旋的节点
     */
    private fun rotateRight(node: TreeNode<T>) {
        val parent = node.parent ?: throw NullPointerException("没有父节点")
        val ancestor = parent.parent

        // 如果要右旋的节点不是父节点的左子节点，则无效右旋
        if (node != parent.left) throw RotateNodeException("要右旋的节点不是父节点的左子节点")

        // 父节点变为要右旋节点的右子节点
        parent.left = node.right
        node.right?.parent = parent
        parent.parent = node
        node.right = parent
        node.parent = ancestor

        // 要右旋节点代替原父节点
        when {
            ancestor?.left == parent -> {
                ancestor.left = node
            }
            ancestor?.right == parent -> {
                ancestor.right = node
            }
            ancestor == null -> {
                root = node
            }
        }
    }

    /**
     * 查找指定值应该放置到哪个节点之下
     *
     * @param target 要插入的值
     * @return 该放在哪个节点下
     */
    private fun findParentNode(target: T): TreeNode<T>? {
        var parent = root
        var child = parent

        while (child != null) {
            when {
                child.value == target -> {
                    return child
                }
                child.value < target -> {
                    parent = child
                    child = parent.right
                }
                else -> {
                    parent = child
                    child = parent.left
                }
            }
        }
        return parent
    }

    /**
     * 获取叔叔节点
     *
     * @param node 谁的叔叔
     * @return 叔叔节点
     */
    private fun getUncle(node: TreeNode<T>): TreeNode<T>? {
        return if (node.parent == node.parent?.parent?.left) {
            node.parent?.parent?.right
        } else {
            node.parent?.parent?.left
        }
    }

    /**
     * 克隆一颗树
     * 节点全是新对象（深克隆）
     */
//    public override fun clone(): RBTree<T> {
//        val clonedTree = RBTree<T>()
//        clonedTree.root = root?.clone() ?: return clonedTree
//        val nodeQueue = ArrayDeque<TreeNode<T>>()
//        nodeQueue.offer(root)
//        while (nodeQueue.isNotEmpty()) {
//            val node = nodeQueue.poll() ?: continue
//            val clonedTreeNode = clonedTree.findParentNode(node.value) ?: continue
//            if (node.left != null) {
//                clonedTreeNode.left = node.left?.clone()
//                nodeQueue.offer(node.left)
//            }
//            if (node.right != null) {
//                clonedTreeNode.right = node.right?.clone()
//                nodeQueue.offer(node.right)
//            }
//        }
//        return clonedTree
//    }

    fun print() {
        val curFloorNode = ArrayDeque<TreeNode<T>>()
        curFloorNode.offer(root)
        val nextFloorNode = ArrayDeque<TreeNode<T>>()
        do {
            var printStr = ""
            while (curFloorNode.isNotEmpty()) {
                val node = curFloorNode.poll() ?: continue
                printStr += "${node.value} ${if (node.isRed) "red" else "black"}"
                if (node.left != null) {
                    nextFloorNode.offer(node.left)
                }
                if (node.right != null) {
                    nextFloorNode.offer(node.right)
                }
            }
            println(printStr)
            curFloorNode.addAll(nextFloorNode)
            nextFloorNode.clear()
        } while (curFloorNode.isNotEmpty())
    }

    class RepeatNodeException(message: String?) : Exception(message)

    class RotateNodeException(message: String?) : Exception(message)

    interface OnNodeChangeListener {
        fun startChange()
        fun onChange()
        fun endChange()
    }

    /**
     * 节点数据类
     */
    data class TreeNode<T : Comparable<T>>(
        override var value: T,
        var isRed: Boolean = false,
        override var parent: TreeNode<T>? = null,
        override var left: TreeNode<T>? = null,
        override var right: TreeNode<T>? = null
    ) : Cloneable, ITreeNode<TreeNode<T>, T> {
        override fun compareTo(other: TreeNode<T>): Int {
            return value.compareTo(other.value)
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is TreeNode<*>) value == other.value else false
        }

        public override fun clone(): TreeNode<T> {
            return TreeNode(value, isRed)
        }

        override fun toString(): String {
            return "{value = $value, isRed = $isRed}"
        }
    }
}