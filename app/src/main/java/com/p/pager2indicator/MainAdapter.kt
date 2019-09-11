package com.p.pager2indicator

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main.view.*

class MainAdapter(var mContext:Context):RecyclerView.Adapter<MainAdapter.MainHolder>() {

    var dataList : ArrayList<String> = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(View.inflate(mContext,R.layout.item_main,null))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        holder.text.text = dataList[position]
    }


    class MainHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val text:TextView = itemView.textView
    }
}