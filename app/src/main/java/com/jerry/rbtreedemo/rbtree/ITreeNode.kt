package com.jerry.rbtreedemo.rbtree

/**
 * 树节点接口
 *
 * @author xujierui
 */
interface ITreeNode<T : ITreeNode<T, V>, V : Comparable<V>> : Comparable<T> {
    val value: V
    val parent: T?
    val left: T?
    val right: T?
}