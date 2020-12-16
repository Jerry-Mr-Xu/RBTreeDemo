package com.jerry.rbtreedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jerry.rbtreedemo.rbtree.Node
import com.jerry.rbtreedemo.rbtree.RBTree
import com.jerry.rbtreedemo.rbtree.RBTree.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tree = RBTree<Int>()
        try {
            for (i in 1 until 11) {
                tree.addNode(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tree.print()
    }
}