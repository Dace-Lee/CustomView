package com.dace.customview

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

class MainActivity : AppCompatActivity() {
    private val content = arrayListOf(
        Menu("小米视频Loading", "com.dace.customview.loading.MiLoadingActivity")
    //more...
    )


    private val simpleAdapter: SimpleAdapter
        get() {
            return SimpleAdapter(this, content).also {
                it.setOnItemClickListener(object : SimpleAdapter.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        startActivity(Intent(this@MainActivity, Class.forName(content[position].router)))
                    }
                })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rvContent = findViewById<RecyclerView>(R.id.rv_content)
        rvContent.layoutManager = LinearLayoutManager(this)
        rvContent.adapter = simpleAdapter
    }
}
