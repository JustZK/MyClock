package com.zk.clock.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import com.zk.clock.R
import com.zk.common.utils.LogUtil

class FlipClockView : FrameLayout {
    private var mVisibleTextView: StrikethroughTextView? = null// 可见的
    private var mInvisibleTextView: StrikethroughTextView? = null // 不可见
    private var mMiddleWidthSize: Float = 5f

    private var mLayoutWidth = 0
    private var mLayoutHeight = 0
    private var mScroller: Scroller? = null

    private val mCamera = Camera()
    private val mMatrix = Matrix()
    private val mTopRect = RectF()
    private val mBottomRect = RectF()
    private var isUpToDown = true
    private val mShinePaint = Paint()
    private val mShadePaint = Paint()
    private var isFlipping = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.textViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            mScroller = Scroller(context, DecelerateInterpolator()) // 减速 // 动画插入器

//            val inflater = LayoutInflater.from(context)
//            inflater.inflate(R.layout.flip_clock_view, this)

            val a = context.obtainStyledAttributes(attrs, R.styleable.FlipClockView)
            val textValue = a.getResourceId(R.styleable.FlipClockView_textValue, R.string.app_name);
            val textSize = a.getDimension(R.styleable.FlipClockView_textSize, 18f)
            mMiddleWidthSize = a.getDimension(R.styleable.FlipClockView_middleWidthSize, 5f)
            val middleWidthColor =
                a.getColor(R.styleable.FlipClockView_middleWidthColor, Color.BLACK)
            val textColor = a.getColor(R.styleable.FlipClockView_textColor, Color.WHITE)
            mVisibleTextView = StrikethroughTextView(context, mMiddleWidthSize, middleWidthColor)
//            mVisibleTextView = findViewById(R.id.flip_clock_visible_tv)
            mInvisibleTextView = StrikethroughTextView(context, mMiddleWidthSize, middleWidthColor)
//            mInvisibleTextView = findViewById(R.id.flip_clock_invisible_tv)

            mVisibleTextView!!.setText(textValue)
            mVisibleTextView!!.textSize = textSize
            mVisibleTextView!!.setTextColor(textColor)
            mVisibleTextView!!.setMiddleWidth(mMiddleWidthSize)
            mVisibleTextView!!.setMiddleColor(middleWidthColor)
            mVisibleTextView!!.gravity = Gravity.CENTER
            mVisibleTextView!!.includeFontPadding = false
            mVisibleTextView!!.setBackgroundResource(R.drawable.time_bg)
//            mVisibleTextView!!.gravity = View.TEXT_ALIGNMENT_CENTER

            mInvisibleTextView!!.setText(textValue)
            mInvisibleTextView!!.textSize = textSize
            mInvisibleTextView!!.setTextColor(textColor)
            mInvisibleTextView!!.setMiddleWidth(mMiddleWidthSize)
            mInvisibleTextView!!.setMiddleColor(middleWidthColor)
            mInvisibleTextView!!.gravity = Gravity.CENTER
            mInvisibleTextView!!.includeFontPadding = false
            mInvisibleTextView!!.setBackgroundResource(R.drawable.time_bg)
//            mInvisibleTextView!!.gravity = View.TEXT_ALIGNMENT_CENTER


            addView(mInvisibleTextView)
            addView(mVisibleTextView)

            val shadePaintColor = a.getColor(R.styleable.FlipClockView_shadePaintColor, Color.BLACK)
            mShadePaint.color = shadePaintColor
//            mShadePaint.style = Paint.Style.FILL
            val shinePaintColor = a.getColor(R.styleable.FlipClockView_shinePaintColor, Color.WHITE)
            mShinePaint.color = shinePaintColor
//            mShinePaint.style = Paint.Style.FILL

            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mLayoutWidth = MeasureSpec.getSize(widthMeasureSpec)
        mLayoutHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(mLayoutWidth, mLayoutHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val count = childCount
        //将两个textView放置进去
        for (i in 0 until count) {
            val child = getChildAt(i)
            child.layout(0, 0, mLayoutWidth, mLayoutHeight)
        }
        mTopRect.top = 0f
        mTopRect.left = 0f
        mTopRect.right = width.toFloat()
        mTopRect.bottom = height / 2 - 5f
        mBottomRect.top = height / 2 + 5f
        mBottomRect.left = 0f
        mBottomRect.right = width.toFloat()
        mBottomRect.bottom = height.toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (!mScroller!!.isFinished && mScroller!!.computeScrollOffset()) {
            drawTopHalf(canvas)
            drawBottomHalf(canvas)
            drawFlipHalf(canvas)
            postInvalidate()
        } else {
            if (isFlipping) {
                showViews(canvas)
            }
            if (mScroller!!.isFinished && !mScroller!!.computeScrollOffset()) {
                isFlipping = false
            }
        }
    }

    //显示需要显示的数字
    private fun showViews(canvas: Canvas) {
        var current = mVisibleTextView!!.text.toString()
        var past = mInvisibleTextView!!.text.toString()
        // Log.e("需要显示的数字--->",current+ "%%    "+past);
        mVisibleTextView!!.text = past
        mInvisibleTextView!!.text = current
        // 防止切换抖动
        drawChild(canvas, mVisibleTextView, 0)
    }

    // 画下半部分
    private fun drawBottomHalf(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(mBottomRect)
        val drawView: View = if (!isUpToDown) mInvisibleTextView!! else mVisibleTextView!!
        drawChild(canvas, drawView, 0)
        canvas.restore()
    }

    // 画上半部分
    private fun drawTopHalf(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(mTopRect)
        val drawView: View = if (!isUpToDown) mVisibleTextView!! else mInvisibleTextView!!
        drawChild(canvas, drawView, 0)
        canvas.restore()
    }

    // 画翻页部分
    private fun drawFlipHalf(canvas: Canvas) {
        canvas.save()
        mCamera.save()
        var view: View?
        val deg: Float = getDeg()
        LogUtil.instance.d("deg ----- $deg")
        view = if (deg > 90) {
            canvas.clipRect(if (!isUpToDown) mTopRect else mBottomRect)
            mCamera.rotateX(if (!isUpToDown) deg - 180 else -(deg - 180))
            mInvisibleTextView
        } else {
            canvas.clipRect(if (!isUpToDown) mBottomRect else mTopRect)
            mCamera.rotateX(if (!isUpToDown) deg else -deg)
            mVisibleTextView
        }
        mCamera.getMatrix(mMatrix)
        positionMatrix()
        canvas.concat(mMatrix)

        if (view != null) {
            drawChild(canvas, view, 0)
        }
        drawFlippingShadeShine(canvas)
        mCamera.restore()
        canvas.restore()
    }

    private fun getDeg(): Float {
        return mScroller!!.currY * 1.0f / mLayoutHeight * 180
    }

    // 绘制翻页时的阳面和阴面
    private fun drawFlippingShadeShine(canvas: Canvas) {
        val degreesFlipped = getDeg()
        // Log.d(TAG, "deg: " + degreesFlipped);
        if (degreesFlipped < 90) {
            val alpha = getAlpha(degreesFlipped)
             LogUtil.instance.d("小于90度时的透明度------------------- $alpha");
            mShinePaint.alpha = alpha
            mShadePaint.alpha = alpha
            canvas.drawRoundRect(
                if (!isUpToDown) mBottomRect else mTopRect, 30f, 30f,
                if (!isUpToDown) mShinePaint else mShadePaint
            )
        } else {
            val alpha = getAlpha(Math.abs(degreesFlipped - 180))
            LogUtil.instance.d("大于90度时的透明度------------- $alpha" );
            mShadePaint.alpha = alpha
            mShinePaint.alpha = alpha
            canvas.drawRoundRect(
                if (!isUpToDown) mTopRect else mBottomRect, 30f, 30f,
                if (!isUpToDown) mShadePaint else mShinePaint
            )
        }
    }

    private fun getAlpha(degreesFlipped: Float): Int {
        return (degreesFlipped / 90f * 100).toInt()
    }

    private fun positionMatrix() {
        mMatrix.preScale(0.25f, 0.25f)
        mMatrix.postScale(4.0f, 4.0f)
        mMatrix.preTranslate(-width / 2.toFloat(), -height / 2.toFloat())
        mMatrix.postTranslate(width / 2.toFloat(), height / 2.toFloat())
    }

    // 初始化隐藏textView显示的值
    private fun initTextView() {
        val visibleValue = mInvisibleTextView!!.text.toString().toInt()
        val invisibleValue = visibleValue - 1
        if (invisibleValue > 0)
            mVisibleTextView!!.text = invisibleValue.toString()
        else mVisibleTextView!!.text = "60"
    }

    /**
     * @param isUpToDown 方向标识 true: 从上往下翻  , false: 从下往上翻
     */
    fun setFlipDirection(isUpToDown: Boolean) {
        this.isUpToDown = isUpToDown
    }

    //平滑翻转
    fun smoothFlip() {
        //Log.e(TAG, "翻动 ");
        initTextView()
        isFlipping = true
        mScroller!!.startScroll(0, 0, 0, mLayoutHeight, 700)
        postInvalidate()
    }

    fun isFlipping(): Boolean {
        return (isFlipping && !mScroller!!.isFinished
                && mScroller!!.computeScrollOffset())
    }

    fun setTextValue(text: String){
        mInvisibleTextView!!.text = text
    }

    fun setFirstTextValue(text: String){
        mVisibleTextView!!.text = text
    }

    /**
     * 设置时间数字的背景
     * @param drawable
     */
    fun setClockBackground(drawable: Drawable?) {
        mVisibleTextView!!.background = drawable
        mInvisibleTextView!!.background = drawable
    }

    /**
     * 设置时间数字的颜色
     * @param color
     */
    fun setClockTextColor(color: Int) {
        mVisibleTextView!!.setTextColor(color)
        mInvisibleTextView!!.setTextColor(color)
    }

    /**
     * 设置时间数字的大小
     * @param size
     */
    fun setClockTextSize(size: Float) {
        mVisibleTextView!!.textSize = size
        mInvisibleTextView!!.textSize = size
    }
}