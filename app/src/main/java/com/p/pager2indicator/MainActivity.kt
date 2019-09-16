package com.p.pager2indicator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        mAdapter = MainAdapter(this)
        viewPager.adapter = mAdapter
        val dataList = ArrayList<String>()
        dataList.add("1")
        dataList.add("2")
        dataList.add("3")
        mAdapter.setData(dataList)

        indicator.useAnim = true
        indicator.bindViewPager2(viewPager)

        indicator.postDelayed({
            dataList.add("4")
            dataList.add("5")
            mAdapter.setData(dataList)
        },5000)
    }
}
