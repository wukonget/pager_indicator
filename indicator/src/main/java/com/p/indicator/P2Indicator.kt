package com.p.indicator

import android.content.Context
import android.database.DataSetObserver
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.min


public open class P2Indicator(var mContext: Context, attrs: AttributeSet?, styleAttr: Int) :
    View(mContext, attrs, styleAttr) {

    private var orientation: Int = 1
    private var mViewPager: ViewPager? = null
    private var mViewPager2: ViewPager2? = null
    private var mFocusPosition: Int = 0
    private var mFocusOffset: Float = 0f

    public val HORIZONTAL = 1
    public val VERTICAL = 2
    public val DOT = 1
    public val LINE = 2
    public val DRAWABLE = 3


    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null, 0)


    private var mode: Int = DOT
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

    var useAnim: Boolean = false


    init {
        if (attrs != null) {
            val atts = mContext.obtainStyledAttributes(attrs, R.styleable.P2Indicator)

            mode = atts.getInt(R.styleable.P2Indicator_p2Mode, DOT)

            itemWidth = atts.getDimensionPixelSize(R.styleable.P2Indicator_p2ItemWidth, 20)
            itemHeight = atts.getDimensionPixelSize(R.styleable.P2Indicator_p2ItemHeight, 20)
            itemGap = atts.getDimensionPixelSize(R.styleable.P2Indicator_p2ItemGap, 30)

            normalColor = atts.getColor(
                R.styleable.P2Indicator_p2NormalColor,
                mContext.resources.getColor(android.R.color.darker_gray)
            )
            selectedColor = atts.getColor(
                R.styleable.P2Indicator_p2SelectedColor,
                mContext.resources.getColor(android.R.color.holo_blue_light)
            )

            normalDrawable = atts.getResourceId(R.styleable.P2Indicator_p2NormalDrawable, 0)
            selectedDrawable = atts.getResourceId(R.styleable.P2Indicator_p2SelectedDrawable, 0)

            orientation = atts.getInt(R.styleable.P2Indicator_p2Orientation, 1)

            atts.recycle()
        }
        initPaint()
    }

    private fun initPaint() {

        normalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        normalPaint.color = normalColor
        normalPaint.strokeWidth = if (LINE == mode) itemHeight.toFloat() else 1f

        selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        selectedPaint.color = selectedColor
        selectedPaint.strokeWidth = if (LINE == mode) itemHeight.toFloat() else 1f

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
                    if (orientation == HORIZONTAL)
                        itemWidth * itemCount + itemGap * (itemCount - 1) + paddingLeft + paddingRight
                    else
                        itemWidth + paddingLeft + paddingRight

            }
            MeasureSpec.EXACTLY -> {
            }
            MeasureSpec.UNSPECIFIED -> {
            }
        }
        when (heightMode) {
            MeasureSpec.AT_MOST -> {
                heightSize = if (orientation == HORIZONTAL)
                    itemHeight + paddingTop + paddingBottom
                else
                    itemHeight * itemCount + itemGap * (itemCount - 1) + paddingTop + paddingBottom
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

        val allViewLength =
            if (orientation == HORIZONTAL)
                itemWidth * itemCount + itemGap * (itemCount - 1) + paddingLeft + paddingRight
            else
                itemWidth * itemCount + itemGap * (itemCount - 1) + paddingTop + paddingBottom

        val startX =
            if (orientation == HORIZONTAL) (width - allViewLength) / 2.toFloat() + paddingLeft else paddingLeft.toFloat()

        val startY =
            if (orientation == HORIZONTAL) paddingTop.toFloat() else (height - allViewLength) / 2.toFloat() + paddingTop

            canvas?.translate(startX, startY)

        drawStatic(canvas)
        drawFocus(canvas)

    }

    private fun drawFocus(canvas: Canvas?) {
        canvas?.save()
        if(orientation == HORIZONTAL) {
            canvas?.translate(
                (itemWidth + itemGap) * mFocusPosition.toFloat() + mFocusOffset * (itemGap + itemWidth),
                0f
            )
        }else{
            canvas?.translate(
                0f,
                (itemHeight + itemGap) * mFocusPosition.toFloat() + mFocusOffset * (itemGap + itemHeight)

            )
        }
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
        if (mode == DRAWABLE) {
            drawable =
                mContext.resources.getDrawable(normalDrawable).toBitmap(itemWidth, itemHeight)
        }

        for (i in 1..itemCount) {
            when (mode) {
                DOT -> {
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
                LINE -> {
                    paint.strokeWidth = itemHeight.toFloat()
                    if(orientation == HORIZONTAL) {
                        canvas?.drawLine(
                            0f,
                            itemHeight / 2.toFloat(),
                            itemWidth.toFloat(),
                            itemHeight / 2.toFloat(),
                            paint
                        )
                    }else{
                        canvas?.drawLine(
                            itemWidth / 2.toFloat(),
                            0f,
                            itemWidth / 2.toFloat(),
                            itemHeight.toFloat(),
                            paint
                        )
                    }
                }
                DRAWABLE -> {
                    paint.strokeWidth = 1f
                    if (drawable != null) {
                        canvas?.drawBitmap(drawable, 0f, 0f, paint)
                    }
                }
            }

            if(orientation == HORIZONTAL) {
                canvas?.translate((itemWidth + itemGap).toFloat(), 0f)
            }else{
                canvas?.translate(0f,(itemHeight + itemGap).toFloat())
            }
        }
    }


    private fun drawItemsFocus(canvas: Canvas?, paint: Paint) {
        var drawable: Bitmap? = null
        var scale = 1f
        if (mode == DRAWABLE) {
            if (selectedDrawable == 0) {
                selectedDrawable = normalDrawable
                selectedPaint.colorFilter =
                    PorterDuffColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
            }
            drawable =
                mContext.resources.getDrawable(if (paint == normalPaint) normalDrawable else selectedDrawable)
                    .toBitmap(itemWidth, itemHeight)
        } else if (mode == LINE && useAnim) {
            scale = 1 + min(((0.5 - abs(mFocusOffset - 0.5)) * 4).toFloat(), 1f)
        }

        if (!customDrawFocusItem(
                canvas,
                paint,
                mode,
                mFocusPosition,
                mFocusOffset,
                itemWidth,
                itemHeight,
                orientation
            )
        ) {
            when (mode) {
                DOT -> {
                    paint.strokeWidth = 1f

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val path = Path()
                        if (useAnim && mFocusOffset > 0f && mFocusOffset < 0.25f) {//离左侧item近
                            if(orientation == HORIZONTAL) {
                                path.addArc(
                                    0f, 0f, itemWidth.toFloat(),
                                    itemHeight.toFloat(),
                                    270f,
                                    180f
                                )
                                path.addArc(
                                    0 - (itemWidth + itemGap) * mFocusOffset,
                                    0f,
                                    itemWidth.toFloat() + (itemWidth + itemGap) * mFocusOffset,
                                    itemHeight.toFloat(),
                                    90f,
                                    180f
                                )
                            }else{
                                path.addArc(
                                    0f, 0f, itemWidth.toFloat(),
                                    itemHeight.toFloat(),
                                    0f,
                                    180f
                                )
                                path.addArc(
                                    0f,
                                    0 - (itemHeight + itemGap) * mFocusOffset,
                                    itemWidth.toFloat(),
                                    itemHeight.toFloat() + (itemHeight + itemGap) * mFocusOffset,
                                    180f,
                                    180f
                                )
                            }
                        } else if (useAnim && mFocusOffset > 0.75f && mFocusOffset < 1f) {//离右侧item近
                            if(orientation == HORIZONTAL) {
                                path.addArc(
                                    0f, 0f, itemWidth.toFloat(), itemHeight.toFloat(),
                                    90f,
                                    180f
                                )
                                path.addArc(
                                    0 - (itemWidth + itemGap) * (1 - mFocusOffset),
                                    0f,
                                    itemWidth.toFloat() + (itemWidth + itemGap) * (1 - mFocusOffset),
                                    itemHeight.toFloat(),
                                    270f,
                                    180f
                                )
                            }else{
                                path.addArc(
                                    0f, 0f, itemWidth.toFloat(), itemHeight.toFloat(),
                                    180f,
                                    180f
                                )
                                path.addArc(
                                    0f,
                                    0 - (itemHeight + itemGap) * (1 - mFocusOffset),
                                    itemWidth.toFloat(),
                                    itemHeight.toFloat() + (itemHeight + itemGap) * (1 - mFocusOffset),
                                    0f,
                                    180f
                                )
                            }
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
                LINE -> {
                    paint.strokeWidth = itemHeight.toFloat() / scale
                    if(orientation == HORIZONTAL) {
                        canvas?.drawLine(
                            0f,
                            itemHeight / 2.toFloat(),
                            itemWidth * scale,
                            itemHeight / 2.toFloat(),
                            paint
                        )
                    }else{
                        canvas?.drawLine(
                            itemWidth / 2.toFloat(),
                            0f,
                            itemWidth / 2.toFloat(),
                            itemHeight * scale,
                            paint
                        )
                    }
                }
                DRAWABLE -> {
                    paint.strokeWidth = 1f
                    if (drawable != null) {
                        canvas?.drawBitmap(drawable, 0f, 0f, paint)
                    }
                }
            }
        }
    }

    /**
     * 用户自己完成item绘制
     * @param mode item形状 DOT,LINE,DRAWABLE
     * @param focusPosition 当前页面
     * @param focusOffset  当前页面偏移量  0~1f
     * @param itemHeight
     * @param itemWidth
     * @param orientation 方向,1水平  2垂直
     */
    open fun customDrawFocusItem(
        canvas: Canvas?,
        paint: Paint,
        mode: Int,
        focusPosition: Int,
        focusOffset: Float,
        itemWidth: Int,
        itemHeight: Int,
        orientation:Int
    ): Boolean {
        return false
    }


    fun bindViewPager2(viewPager: ViewPager2) {
        mViewPager2 = viewPager
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
        viewPager.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                update(false)
                super.onChanged()
            }
        })
        update()
    }

    private fun update(onlyDraw: Boolean = true) {
        itemCount =
            if (mViewPager != null) mViewPager!!.adapter!!.count else (if (mViewPager2 != null) mViewPager2!!.adapter!!.itemCount else 1)
        if (onlyDraw) postInvalidate() else requestLayout()
    }

    fun bindViewPager(viewPager: ViewPager) {
        mViewPager = viewPager
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
        viewPager.adapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                update(false)
                super.onChanged()
            }
        })
        update()
    }

}