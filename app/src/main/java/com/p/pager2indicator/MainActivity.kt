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
        dataList.add("111")
        dataList.add("222")
        dataList.add("333")
//        dataList.add("444")
//        dataList.add("555")
        mAdapter.dataList = dataList

        indicator.bindViewPager2(viewPager)
    }
}
