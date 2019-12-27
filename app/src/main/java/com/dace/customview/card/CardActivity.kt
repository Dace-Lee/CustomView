package com.dace.customview.card

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.DefaultItemAnimator
import com.dace.customview.R


class CardActivity : AppCompatActivity() {

    private val img = arrayListOf(
        "http://up.deskcity.org/pic_source/27/83/03/278303d24111034dc0e43431a8b6f1af.jpg",
        "http://img3.imgtn.bdimg.com/it/u=2683470453,2952552187&fm=214&gp=0.jpg",
        "http://wx3.sinaimg.cn/large/007uIhLDly1g7xigdlnmyj30sg0g00xk.jpg",
        "http://i1.chexun.net/images/2017/0609/22938/news_0_0_CCDB49AFB357F5C7539310D1B006CDE9.jpg",
        "http://www.at188.com/autolibrary/images/cadillac/cts/1.jpg",
        "http://image2.cnpp.cn/upload/images/20180922/14555225013_840x600.jpg",
        "http://n.sinaimg.cn/sinacn20110/431/w1267h764/20181226/fc8d-hqtwzec5040874.jpg",
        "http://n.sinaimg.cn/sinacn/20170609/06b7-fyfzhac0741423.png"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        initView()
    }

    private fun initView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_content)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = CardAdapter(this, img)
        val cardCallback: ItemTouchHelperCallback<String> =
            ItemTouchHelperCallback(img,recyclerView.adapter!!, object : OnSwipeListener<String> {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, t: String, direction: Int) {

                }

                override fun onSwiping(viewHolder: RecyclerView.ViewHolder, ratio: Float, direction: Int) {

                }

                override fun onSwipedClear() {

                }

            })
        val touchHelper = ItemTouchHelper(cardCallback)
        val cardLayoutManager = CardLayoutManager(recyclerView, touchHelper)
        recyclerView.layoutManager = cardLayoutManager
        touchHelper.attachToRecyclerView(recyclerView)
    }
}