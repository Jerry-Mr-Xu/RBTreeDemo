package com.jerry.rbtreedemo.rbtree

import com.jerry.rbtreedemo.rbtree.RBTree.*
import com.jerry.rbtreedemo.rbtree.RBTreeView.*

class TreeHelper {
    companion object {
        /**
         * 将红黑树转为UI树
         *
         * @param realTree 红黑树
         */
        fun <T : Comparable<T>> transformRBTree2ViewTree(realTree: RBTree<T>): TreeViewNode<T>? {
            return transformTreeNode2ViewNode(realTree.root)
        }

        private fun <T : Comparable<T>> transformTreeNode2ViewNode(treeNode: TreeNode<T>?): TreeViewNode<T>? {
            treeNode ?: return null
            val leftViewNode = transformTreeNode2ViewNode(treeNode.left)
            val rightViewNode = transformTreeNode2ViewNode(treeNode.right)
            val parentViewNode = TreeViewNode(value = treeNode.value, isRed = treeNode.isRed)
            leftViewNode?.parent = parentViewNode
            rightViewNode?.parent = parentViewNode
            parentViewNode.left = leftViewNode
            parentViewNode.right = rightViewNode
            return parentViewNode
        }

        /**
         * 将树平铺
         */
        fun <T : ITreeNode<T, V>, V: Comparable<V>> flatTree(rootNode: T?): HashMap<V, T> {
            val nodeMap = HashMap<V, T>()
            flatTreeToMap(nodeMap, rootNode)
            return nodeMap
        }

        /**
         * 将树平铺为集合
         */
        @Suppress("UNCHECKED_CAST")
        private fun <T : ITreeNode<T, V>, V: Comparable<V>> flatTreeToMap(nodeMap: HashMap<V, T>, rootNode: T?) {
            rootNode ?: return
            nodeMap[rootNode.value] = rootNode
            flatTreeToMap(nodeMap, rootNode.left)
            flatTreeToMap(nodeMap, rootNode.right)
        }
    }
}