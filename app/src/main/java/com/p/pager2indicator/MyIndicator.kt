package com.p.pager2indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import com.p.indicator.P2Indicator

class MyIndicator(context: Context, attrs:AttributeSet?, styleAttr:Int):P2Indicator(context, attrs,styleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)

    override fun customDrawFocusItem(
        canvas: Canvas?,
        paint: Paint,
        mode: Int,
        focusPosition: Int,
        focusOffset: Float,
        itemWidth:Int,
        itemHeight:Int,
        orientation:Int
    ): Boolean {
        canvas?.drawCircle(20f,itemHeight/2.toFloat(),30f,paint)
        return true
    }
}