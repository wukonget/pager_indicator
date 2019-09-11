package com.p.indicator

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.viewpager2.widget.ViewPager2
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


public class P2Indicator(var mContext: Context, var attrs: AttributeSet?, var styleAttr: Int) :
    View(mContext, attrs, styleAttr) {

    private var mFocusPosition: Int = 0
    private var mFocusOffset: Float = 0f
    public val MODE_DOT = 1
    public val MODE_LINE = 2
    public val MODE_DRAWABLE = 3


    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)


    private var mode: Int = MODE_DOT
    private var itemCount: Int = 3
    private var itemWidth: Int = 10
    private var itemHeight: Int = 10
    private var itemGap: Int = 10
    private var normalColor: Int = Color.GRAY
    private var selectedColor: Int = Color.BLACK
    private var normalDrawable: Int = 0
    private var selectedDrawable: Int = 0
    private lateinit var normalPaint: Paint
    private lateinit var selectedPaint: Paint


    init {
        if (attrs != null) {
            val atts = mContext.obtainStyledAttributes(attrs, R.styleable.P2Indicator)

            mode = atts.getInt(R.styleable.P2Indicator_p2Mode, MODE_DOT)

            itemWidth = atts.getDimensionPixelSize(R.styleable.P2Indicator_p2ItemWidth, 30)
            itemHeight = atts.getDimensionPixelSize(R.styleable.P2Indicator_p2ItemHeight, 30)
            itemGap = atts.getDimensionPixelSize(R.styleable.P2Indicator_p2ItemGap, 50)

            normalColor = atts.getColor(R.styleable.P2Indicator_p2NormalColor, Color.GRAY)
            selectedColor = atts.getColor(R.styleable.P2Indicator_p2SelectedColor, Color.GREEN)

            normalDrawable = atts.getResourceId(R.styleable.P2Indicator_p2NormalDrawable, 0)
            selectedDrawable = atts.getResourceId(R.styleable.P2Indicator_p2SelectedDrawable, 0)

            atts.recycle()
        }
        initPaint()
    }

    private fun initPaint() {

        normalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        normalPaint.color = normalColor
        normalPaint.strokeWidth = if (MODE_LINE == mode) itemHeight.toFloat() else 1f

        selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        selectedPaint.color = selectedColor
        selectedPaint.strokeWidth = if (MODE_LINE == mode) itemHeight.toFloat() else 1f

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        when (widthMode) {
            MeasureSpec.AT_MOST -> {
                widthSize =
                    itemWidth * itemCount + itemGap * (itemCount - 1) + paddingLeft + paddingRight
            }
            MeasureSpec.EXACTLY -> {
            }
            MeasureSpec.UNSPECIFIED -> {
            }
        }
        when (heightMode) {
            MeasureSpec.AT_MOST -> {
                heightSize = itemHeight + paddingTop + paddingBottom
            }
            MeasureSpec.EXACTLY -> {
            }
            MeasureSpec.UNSPECIFIED -> {
            }
        }

        setMeasuredDimension(widthSize, heightSize)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val allViewWidth =
            itemWidth * itemCount + itemGap * (itemCount - 1) + paddingLeft + paddingRight

        var startX = (width - allViewWidth) / 2.toFloat() + paddingLeft

        canvas?.translate(startX, paddingTop.toFloat())

        drawStatic(canvas)
        drawFocus(canvas)

    }

    private fun drawFocus(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(
            (itemWidth + itemGap) * mFocusPosition.toFloat() + mFocusOffset * (itemGap + itemWidth),
            0f
        )
        drawItemsFocus(canvas, selectedPaint)
        canvas?.restore()
    }

    /**
     * 画固定的几个item
     */
    private fun drawStatic(canvas: Canvas?) {
        canvas?.save()
        drawItems(canvas, normalPaint)
        canvas?.restore()
    }

    private fun drawItems(canvas: Canvas?, paint: Paint) {
        var drawable: Bitmap? = null
        if (mode == MODE_DRAWABLE) {
            drawable =
                mContext.resources.getDrawable(normalDrawable).toBitmap(itemWidth, itemHeight)
        }

        for (i in 1..itemCount) {
            when (mode) {
                MODE_DOT -> {
                    paint.strokeWidth = 1f
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        canvas?.drawOval(0f, 0f, itemWidth.toFloat(), itemHeight.toFloat(), paint)
                    } else {
                        canvas?.drawCircle(
                            itemWidth / 2.toFloat(),
                            itemHeight / 2.toFloat(),
                            itemWidth / 2.toFloat(),
                            paint
                        )
                    }
                }
                MODE_LINE -> {
                    paint.strokeWidth = itemHeight.toFloat()
                    canvas?.drawLine(
                        0f,
                        itemHeight / 2.toFloat(),
                        itemWidth.toFloat(),
                        itemHeight / 2.toFloat(),
                        paint
                    )
                }
                MODE_DRAWABLE -> {
                    paint.strokeWidth = 1f
                    if (drawable != null) {
                        canvas?.drawBitmap(drawable, 0f, 0f, paint)
                    }
                }
            }

            canvas?.translate((itemWidth + itemGap).toFloat(), 0f)
        }
    }


    private fun drawItemsFocus(canvas: Canvas?, paint: Paint) {
        var drawable: Bitmap? = null
        if (mode == MODE_DRAWABLE) {
            if (selectedDrawable == 0) {
                selectedDrawable = normalDrawable
                selectedPaint.colorFilter =
                    PorterDuffColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
            }
            drawable =
                mContext.resources.getDrawable(if (paint == normalPaint) normalDrawable else selectedDrawable)
                    .toBitmap(itemWidth, itemHeight)
        }

        val scale = 1 + min(((0.5 - abs(mFocusOffset - 0.5)) * 4).toFloat(), 1f)

        when (mode) {
            MODE_DOT -> {
                paint.strokeWidth = 1f

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val path = Path()
                    if (mFocusOffset > 0f && mFocusOffset < 0.25f) {//离左侧item近
                        path.addArc(
                            0f, 0f, itemWidth.toFloat(),
                            itemHeight.toFloat(),
                            270f,
                            180f
                        )
                        path.addArc(0-(itemWidth+itemGap)*mFocusOffset,0f,itemWidth.toFloat()+(itemWidth+itemGap)*mFocusOffset,itemHeight.toFloat(),90f,180f)

                    } else if (mFocusOffset > 0.75f && mFocusOffset < 1f) {//离右侧item近
                        path.addArc(
                            0f, 0f, itemWidth.toFloat(), itemHeight.toFloat(),
                            90f,
                            180f
                        )
                        path.addArc(0-(itemWidth+itemGap)*(1-mFocusOffset),0f,itemWidth.toFloat()+(itemWidth+itemGap)*(1-mFocusOffset),itemHeight.toFloat(),270f,180f)
                    } else {
                        canvas?.drawOval(
                            0f,
                            0f,
                            itemWidth.toFloat(),
                            itemHeight.toFloat(),
                            paint
                        )
                    }
                    paint.style = Paint.Style.FILL
                    canvas?.drawPath(path, paint)
                } else {
                    canvas?.drawCircle(
                        itemWidth / 2.toFloat(),
                        itemHeight / 2.toFloat(),
                        itemWidth / 2.toFloat(),
                        paint
                    )
                }
            }
            MODE_LINE -> {
                paint.strokeWidth = itemHeight.toFloat() / scale
                canvas?.drawLine(
                    0f,
                    itemHeight / 2.toFloat(),
                    itemWidth * scale,
                    itemHeight / 2.toFloat(),
                    paint
                )
            }
            MODE_DRAWABLE -> {
                paint.strokeWidth = 1f
                if (drawable != null) {
                    canvas?.drawBitmap(drawable, 0f, 0f, paint)
                }
            }
        }

        canvas?.translate((itemWidth + itemGap).toFloat(), 0f)
    }

    fun bindViewPager2(viewPager: ViewPager2) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                mFocusPosition = position
                mFocusOffset = positionOffset
                postInvalidate()
            }
        })
        itemCount = viewPager.adapter!!.itemCount
        postInvalidate()
    }

    fun bindViewPager(viewPager: ViewPager) {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                mFocusPosition = position
                mFocusOffset = positionOffset
                postInvalidate()
            }
        })
        itemCount = viewPager.adapter!!.count
        postInvalidate()
    }

}