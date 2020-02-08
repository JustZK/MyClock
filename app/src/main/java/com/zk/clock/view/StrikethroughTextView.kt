package com.zk.clock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.zk.clock.R

class StrikethroughTextView : AppCompatTextView {
    private var mPaint: Paint? = null
    private var mMiddleWidthSize: Float = 5f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.textViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.StrikethroughTextView)
            mMiddleWidthSize =
                a.getDimension(R.styleable.StrikethroughTextView_middleWidthSize, 5f)
            val color =
                a.getColor(R.styleable.StrikethroughTextView_middleWidthColor, Color.BLACK)
            initPaint(color)
            a.recycle()
        }
    }

    constructor(
        context: Context?,
        middleWidthSize: Float,
        middleWidthColor: Int
    ) : super(context) {
        mMiddleWidthSize = middleWidthSize
        initPaint(middleWidthColor)
    }

    private fun initPaint(color: Int) {
        if (mPaint == null) {
            mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            mPaint!!.style = Paint.Style.FILL
        }
        mPaint!!.color = color
    }

    fun setMiddleWidth(size: Float) {
        mMiddleWidthSize = size
        postInvalidate()
    }

    fun setMiddleColor(color: Int) {
        mPaint!!.color = color
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(
            0f,
            measuredHeight / 2.toFloat() - mMiddleWidthSize,
            measuredWidth.toFloat(),
            measuredHeight / 2.toFloat() + mMiddleWidthSize,
            mPaint!!
        )
    }
}