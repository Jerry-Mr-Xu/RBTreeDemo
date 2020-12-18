package com.jerry.rbtreedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.jerry.rbtreedemo.rbtree.Node
import com.jerry.rbtreedemo.rbtree.RBTree
import com.jerry.rbtreedemo.rbtree.RBTree.*
import com.jerry.rbtreedemo.rbtree.RBTreeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var tree = RBTree<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rbTreeView = findViewById<RBTreeView>(R.id.rb_tree)
        rbTreeView.rbTree = tree
        tree.onChangeListener = object : OnNodeChangeListener {
            var delayTime = 0L

            override fun startChange() {
                delayTime = 0L
            }

            override fun onChange() {
                rbTreeView.rbTree = tree
                delayTime += 1000L
            }

            override fun endChange() {
                rbTreeView.rbTree = tree
                delayTime = 0L
            }

        }

        val etNodeValue = findViewById<EditText>(R.id.et_node_value)
        val btnAddNode = findViewById<Button>(R.id.btn_add_node)
        btnAddNode.setOnClickListener {
            try {
                tree.addNode(etNodeValue.text.toString().toInt())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}