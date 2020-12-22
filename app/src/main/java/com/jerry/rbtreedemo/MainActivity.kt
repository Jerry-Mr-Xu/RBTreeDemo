package com.jerry.rbtreedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.jerry.rbtreedemo.rbtree.RBTree
import com.jerry.rbtreedemo.rbtree.RBTree.*
import com.jerry.rbtreedemo.rbtree.RBTreeView
import com.jerry.rbtreedemo.rbtree.TreeHelper
import kotlinx.coroutines.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var scope: CoroutineScope

    var tree = RBTree<Int>()
    var isChanging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scope = CoroutineScope(Dispatchers.Main)
        val rbTreeView = findViewById<RBTreeView>(R.id.rb_tree)
        tree.onChangeListener = object : OnNodeChangeListener {
            var delayTime = -1000L

            override fun startChange() {
                isChanging = true
                delayTime = -1000L
                val viewTreeRoot = TreeHelper.transformRBTree2ViewTree(tree)
                rbTreeView.rootNode = viewTreeRoot
            }

            override fun onChange() {
                val viewTreeRoot = TreeHelper.transformRBTree2ViewTree(tree)
                scope.launch {
                    Log.i(TAG, "onChange: before")
                    delayTime += 1000L
                    delay(delayTime)
                    Log.i(TAG, "onChange: after")
                    rbTreeView.rootNode = viewTreeRoot
                }
            }

            override fun endChange() {
                val viewTreeRoot = TreeHelper.transformRBTree2ViewTree(tree)
                scope.launch {
                    Log.i(TAG, "endChange: before")
                    delayTime += 1000L
                    delay(delayTime)
                    Log.i(TAG, "endChange: after")
                    rbTreeView.rootNode = viewTreeRoot
                    isChanging = false
                }
            }
        }

        val etNodeValue = findViewById<EditText>(R.id.et_node_value)
        val btnAddNode = findViewById<Button>(R.id.btn_add_node)
        btnAddNode.setOnClickListener {
            if (isChanging) return@setOnClickListener
            try {
                tree.addNode(etNodeValue.text.toString().toInt())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}