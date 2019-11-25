package com.dace.customview.loading

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import android.util.AttributeSet

/**
 * 小米视频 loading 仿写
 * 整个动画分为 9 个进度阶段，每个进度阶段时间为300ms
 * todo 暂未对外部提供定制属性，动画循环时会有部分三角形出现闪动，待修复
 */
class LoadingView constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val TAG = "LoadingView"

    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    /**
     * 四个三角形的中心坐标，因为显示和隐藏的角度不同，顺序也不同，所以有八个值
     */
    private lateinit var points: ArrayList<PointF>
    /**
     * 四个三角形的旋转角度，因为显示和隐藏的角度不同，顺序也不同，所以有八个值
     */
    private lateinit var angles: ArrayList<Int>
    /**
     * 宽高默认值
     */
    private val DEFAULT_SIZE = 1000
    /**
     * 单个三角形显示隐藏的动画时长
     */
    private val duration = 300L
    /**
     * 四个三角形的颜色
     */
    private val colors = arrayOf(
        Color.parseColor("#B393CC"),
        Color.parseColor("#EEAD45"),
        Color.parseColor("#71B6C0"),
        Color.parseColor("#E37F7C"),
        Color.parseColor("#B393CC"),
        Color.parseColor("#71B6C0"),
        Color.parseColor("#EEAD45"),
        Color.parseColor("#E37F7C")
    )
    /**
     * 根号3常量
     */
    private val v3 = Math.sqrt(3.0).toFloat()
    /**
     * true:显示阶段  false:隐藏阶段
     */
    var showStatus = true
    /**
     * 0:中间出现 1:上面出现 2:右面出现 3:下面出现  4:静止不动  5:下面消失  6:上面消失  7:右面消失  8:中间消失
     * 对应 count
     * 1         2         3         4          4          4          3         2          1
     */
    var step = 0
    /**
     * 控制单个三角形动画的进度
     */
    private var progress = 0f
    /**
     * 画笔
     */
    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /**
     * 宽高尺寸
     */
    private var size: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width = 0
        var height = 0
        if (widthMode == MeasureSpec.AT_MOST) {
            width = if (DEFAULT_SIZE > widthSize) widthSize else DEFAULT_SIZE
        } else if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = if (DEFAULT_SIZE > heightSize) heightSize else DEFAULT_SIZE
        } else if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        size = measuredWidth
        points = arrayListOf(//1234 4231
            PointF(size * (1f - 1f / v3), size / 2f),
            PointF(size * (1 - v3 / 2f + 1f / 4f / v3), size / 4f),
            PointF(size * (1 - 1f / 2f / v3), size / 2f),
            PointF(size * (1 - v3 / 2f + 1f / 4f / v3), size * 3f / 4f),
            PointF(size * (1f - 1f / v3), size / 2f),
            PointF(size * (1 - 1f / 2f / v3), size / 2f),
            PointF(size * (1 - v3 / 2f + 1f / 4f / v3), size / 4f),
            PointF(size * (1 - v3 / 2f + 1f / 4f / v3), size * 3f / 4f)
        )
        angles = arrayListOf(//1234 4231
            -30, 30, 150, -90,
            -150, 30, -90, 150
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        anim()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val count = if (step <= 3) step + 1 else if (step == 4) 4 else 9 - step
        for (index in 0 until count) { //count 取值 1、2、3、4
            val i = if (showStatus) index else index + 4
            canvas?.let {
                drawTriangle(
                    index == count - 1,
                    colors[i],
                    points[i],
                    angles[i],
                    progress,
                    it
                )
            }
        }
    }

    /**
     * 画三角形
     */
    private fun drawTriangle(anim: Boolean, color: Int, point: PointF, angle: Int, progress: Float, canvas: Canvas) {
        canvas.save()
        val tempProgress = if (showStatus) 1 - progress else progress
        val currentProgress = if (step != 4 && anim) tempProgress else 0f
        val path = getTrianglePath()
        val showClipPath = getClipPath(currentProgress)
        canvas.translate(point.x, point.y)
        canvas.rotate(angle.toFloat())
        canvas.clipPath(showClipPath)
        paint.color = color
        canvas.drawPath(path, paint)
        canvas.restore()
    }

    /**
     * clip区域path
     */
    private fun getClipPath(currentProgress: Float): Path {
        val showClipPath = Path()
        showClipPath.moveTo(-size / 4f, -size / 4f / v3 + currentProgress * (size / 2f / v3 + size / 4f / v3))
        showClipPath.lineTo(size / 4f, -size / 4f / v3 + currentProgress * (size / 2f / v3 + size / 4f / v3))
        showClipPath.lineTo(size / 4f, size / 2f / v3)
        showClipPath.lineTo(-size / 4f, size / 2f / v3)
        showClipPath.close()
        return showClipPath
    }

    /**
     * 三角形path
     */
    private fun getTrianglePath(): Path {
        val path = Path()
        path.moveTo(-size / 4f, -size / 4f / v3)
        path.lineTo(size / 4f, -size / 4f / v3)
        path.lineTo(0f, size / 2f / v3)
        path.close()
        return path
    }

    /**
     * 动画
     */
    private fun anim() {
        val animator = ObjectAnimator.ofFloat(this, "progress", 0f, 1f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.duration = duration
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
                step++
                step %= 9
                showStatus = step <= 4
            }
        })
        animator.start()
    }

    /**
     * 属性动画需要的方法
     */
    fun setProgress(process: Float) {
        this.progress = process
        postInvalidate()
    }
}