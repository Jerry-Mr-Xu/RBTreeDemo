package com.jerry.rbtreedemo.rbtree

import java.lang.Exception
import java.lang.NullPointerException

class RBTree<T : Comparable<T>> {
    private var root: Node<T>? = null
    var size = 0

    /**
     * 插入节点
     *
     * @param value 节点的值
     * @throws RepeatNodeException 重复节点异常
     */
    @Throws(RepeatNodeException::class, RotateNodeException::class, NullPointerException::class)
    fun addNode(value: T) {
        if (root == null) {
            root = Node(value)
            size++
            return
        }

        val parent = findParentNode(value) ?: throw NullPointerException("没有父节点")
        if (parent.value == value) {
            // 如果和父节点的值重复则抛出异常
            throw RepeatNodeException("树中有相同值的节点")
        }

        val newNode = Node(value, true, parent)
        if (newNode < parent) {
            parent.left = newNode
        } else {
            parent.right = newNode
        }

        fixInsert(newNode)
        root?.isRed = false
        size++
    }

    /**
     * 插入修复
     * 通过旋转变色等方式修复为满足红黑树规则
     *
     * @param insertNode 插入的节点
     */
    private fun fixInsert(insertNode: Node<T>) {
        var node = insertNode
        var parent = node.parent

        while (parent != null && parent.isRed) {
            val uncle = getUncle(node)
            if (uncle == null) {
                // 如果叔叔为空
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
                        rotateRight(node)
                        // 把子变黑，把祖变红
                        node.isRed = false
                        node.right?.isRed = true
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
                        // 把父变黑，把祖变红
                        parent.isRed = false
                        parent.right?.isRed = true
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
                        rotateLeft(node)
                        // 把子变黑，把祖变红
                        node.isRed = false
                        node.left?.isRed = true
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
                        // 把父变黑，把祖变红
                        parent.isRed = false
                        parent.left?.isRed = true
                    }
                }
                parent = null
            } else {
                parent.isRed = false
                uncle.isRed = false
                parent.parent?.isRed = true
                node = parent
                parent = parent.parent
            }
        }
    }

    /**
     * 左旋操作
     *
     * @param node 要左旋的节点
     */
    private fun rotateLeft(node: Node<T>) {
        val parent = node.parent ?: throw NullPointerException("没有父节点")
        val ancestor = parent.parent

        // 如果要左旋的节点不是父节点的右子节点，则无效左旋
        if (node != parent.right) throw RotateNodeException("要左旋的节点不是父节点的右子节点")

        // 父节点变为要左旋节点的左子节点
        parent.right = node.left
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
    private fun rotateRight(node: Node<T>) {
        val parent = node.parent ?: throw NullPointerException("没有父节点")
        val ancestor = parent.parent

        // 如果要右旋的节点不是父节点的左子节点，则无效右旋
        if (node != parent.left) throw RotateNodeException("要右旋的节点不是父节点的左子节点")

        // 父节点变为要右旋节点的右子节点
        parent.left = node.right
        parent.parent = node
        node.right = parent

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
    private fun findParentNode(target: T): Node<T>? {
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
    private fun getUncle(node: Node<T>): Node<T>? {
        return if (node.parent == node.parent?.parent?.left) {
            node.parent?.parent?.right
        } else {
            node.parent?.parent?.left
        }
    }

    fun print() {
        val nodeQueue = java.util.ArrayDeque<Node<T>>()
        nodeQueue.offer(root)
        while (nodeQueue.isNotEmpty()) {
            val node = nodeQueue.poll() ?: continue
            println("${node.value} ${if (node.isRed) "r" else "b"}")
            if (node.left != null) {
                nodeQueue.offer(node.left)
            }
            if (node.right != null) {
                nodeQueue.offer(node.right)
            }
        }
    }

    class RepeatNodeException(message: String?) : Exception(message) {

    }

    class RotateNodeException(message: String?) : Exception(message) {

    }
}