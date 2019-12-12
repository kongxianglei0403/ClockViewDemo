package com.kxl.clockviewdemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntegerRes
import java.text.SimpleDateFormat
import java.util.*

/**
 *Create by atu on 2019/12/12
 */
class ClockView: View {

    //圆的画笔
    private var mPaint: Paint? = null
    private var mTextPaint: Paint? = null//文字
    private var mHourPaint: Paint? = null//时
    private var mMinutePaint: Paint? = null//分
    private var mSecondPaint: Paint? = null//秒
    //圆心坐标
    private var mX = 0f
    private var mY = 0f
    //半径
    private var mR = 0
    private val startTxt = "12"

    constructor(context: Context): super(context){
        initView()
    }

    constructor(context: Context,attributeSet: AttributeSet): super(context,attributeSet){
        initView()
    }

    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr: Int): super(context,attributeSet,defStyleAttr){
        initView()
    }

    private fun initView() {
        mPaint = Paint()
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeWidth = 3f
        mPaint!!.isAntiAlias = true
        mPaint!!.color = Color.BLACK

        mTextPaint = Paint()
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = Color.BLACK
        mTextPaint!!.textSize = 35f
        mTextPaint!!.style = Paint.Style.STROKE
        mTextPaint!!.textAlign = Paint.Align.CENTER

        mHourPaint = Paint()
        mHourPaint!!.isAntiAlias = true
        mHourPaint!!.color = Color.BLACK
        mHourPaint!!.strokeWidth = 13f
        mHourPaint!!.style = Paint.Style.FILL

        mMinutePaint = Paint()
        mMinutePaint!!.color = Color.BLACK
        mMinutePaint!!.isAntiAlias = true
        mMinutePaint!!.style = Paint.Style.FILL
        mMinutePaint!!.strokeWidth = 8f

        mSecondPaint = Paint()
        mSecondPaint!!.color = Color.RED
        mSecondPaint!!.isAntiAlias = true
        mSecondPaint!!.style = Paint.Style.FILL
        mSecondPaint!!.strokeWidth = 5f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mX = (measuredWidth / 2).toFloat()
        mY = (measuredHeight / 2).toFloat()
        mR = mX.toInt() - 10
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //1 绘制圆
        canvas.drawCircle(mX,mY,mR.toFloat(),mPaint!!)
        //2绘制圆心
        canvas.drawCircle(mX,mY,15f,mMinutePaint!!)
        canvas.drawText("劳力士",mX - 10,mY - mR + 150,mTextPaint!!)

        //3绘制刻度
        drawLines(canvas)
        //4绘制整点
        drawText(canvas)
        //5 更新时间
        updateCurrentTime(canvas)
    }

    /**
     * 绘制时 分 刻度
     * 从12（0）时 开始 画一次 围绕圆心旋转6°
     */
    private fun drawLines(canvas: Canvas) {
        for (index in 0 until 60){
            if (index % 5 == 0){
                mPaint!!.strokeWidth = 8f
                canvas.drawLine(mX,mY - mR,mX,mY - mR + 40,mPaint!!)
            }
            else{
                mPaint!!.strokeWidth = 5f
                canvas.drawLine(mX,mY - mR,mX,mY - mR + 30,mPaint!!)
            }
            canvas.rotate(6f,mX,mY)
        }
    }

    /**
     * 绘制 整点文字
     */
    private fun drawText(canvas: Canvas){
        //获取文字的大小
        val textSize = mTextPaint!!.fontMetrics.bottom - mTextPaint!!.fontMetrics.top
        //文字到圆心的距离 40 是刻度的长度 20就是大小
        val distance = mR - 40 - 20
        //整点坐标
        var a = 0f
        var b = 0f
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.textSize = 50f
        mTextPaint!!.style = Paint.Style.STROKE
        mTextPaint!!.strokeWidth = 5f
        for (i in 0 until 12){
            a = (Math.sin(i.toDouble() * 30.0 * Math.PI / 180) * distance + mX).toFloat()
            b = (mY - Math.cos(i.toDouble() * 30.0 * Math.PI / 180) * distance).toFloat()
            if (i == 0){
                canvas.drawText(startTxt,a,b + textSize / 3,mTextPaint!!)
            }
            else{
                canvas.drawText(i.toString(),a,b + textSize / 3,mTextPaint!!)
            }
        }
    }

    /**
     * 获取系统时间 显示
     * 以12点为起点0° 为参照
     */
    @SuppressLint("SimpleDateFormat")
    private fun updateCurrentTime(canvas: Canvas){
        val format = SimpleDateFormat("HH-mm-ss")
        val time = format.format(Date(System.currentTimeMillis()))
        val split = time.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val hour = Integer.parseInt(split[0])
        val minute = Integer.parseInt(split[1])
        val second = Integer.parseInt(split[2])

        //时针走过的角度
        val hourAngle = hour * 30 + minute / 2//60分钟/2 是时针的角度
        val minuteAngle = minute * 6 + second / 10
        val secondAngle = second * 6

        //绘制时钟
        drawLine(canvas,hourAngle,170,mHourPaint!!)
        //绘制分针
        drawLine(canvas,minuteAngle,60,mMinutePaint!!)
        //秒针
        canvas.rotate(secondAngle.toFloat(),mX,mY)
        canvas.drawLine(mX,mY,mX,mY - mR + 80,mSecondPaint!!)

        postInvalidateDelayed(1000)//1秒刷新一次
    }

    private fun drawLine(canvas: Canvas, angle: Int, length: Int, paint: Paint) {
        canvas.rotate(angle.toFloat(),mX,mY)
        canvas.drawLine(mX,mY,mX,mY - mR + length,paint)
        canvas.save()
        canvas.restore()
        //回转angle 从0°开始
        canvas.rotate((-angle).toFloat(),mX,mY)
    }
}