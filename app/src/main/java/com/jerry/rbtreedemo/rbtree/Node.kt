package com.jerry.rbtreedemo.rbtree

data class Node<T : Comparable<T>>(
    var value: T,
    var isRed: Boolean = false,
    var parent: Node<T>? = null,
    var left: Node<T>? = null,
    var right: Node<T>? = null
) : Comparable<Node<T>>, Cloneable {
    override fun compareTo(other: Node<T>): Int {
        return value.compareTo(other.value)
    }

    public override fun clone(): Node<T> {
        return Node(value, isRed)
    }
}
