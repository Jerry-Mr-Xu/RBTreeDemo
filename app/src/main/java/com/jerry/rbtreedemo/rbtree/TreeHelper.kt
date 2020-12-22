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
         * 通过根节点查找和指定节点相同值的节点
         *
         * @param rootNode 根节点
         * @param targetNode 指定节点
         */
        fun findTreeNodeFromRoot(rootNode: ITreeNode<*>?, targetNode: ITreeNode<*>): ITreeNode<*>? {
            rootNode ?: return null
            if (rootNode == targetNode) {
                return rootNode
            }
            val leftResult = findTreeNodeFromRoot(rootNode.left, targetNode)
            if (leftResult != null) {
                return leftResult
            }
            return findTreeNodeFromRoot(rootNode.right, targetNode)
        }

        /**
         * 将树平铺
         */
        fun <T: ITreeNode<*>> flatTree(rootNode: T?): Set<T> {
            val nodeSet = HashSet<T>()
            flatTreeToSet(nodeSet, rootNode)
            return nodeSet
        }

        /**
         * 将树平铺为集合
         */
        @Suppress("UNCHECKED_CAST")
        private fun<T: ITreeNode<*>> flatTreeToSet(nodeSet: HashSet<T>, rootNode: T?) {
            rootNode ?: return
            nodeSet.add(rootNode)
            if (rootNode.left != null) {
                flatTreeToSet(nodeSet, rootNode.left as T)
            }
            if (rootNode.right != null) {
                flatTreeToSet(nodeSet, rootNode.right as T)
            }
        }
    }
}