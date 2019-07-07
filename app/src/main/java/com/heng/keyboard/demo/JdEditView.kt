package com.heng.keyboard.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.heng.keyboard.KeyBoardUtils

class JdEditView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //文字个数 6个
    private var textNum = 6
    private val textPaint = Paint()
    private val boxPaint = Paint()
    private var showText = StringBuilder("")
    private val textInfos: List<TextInfo>
    var verifyCallBack: ((text: String) -> Unit)? = null

    init {
        val list = mutableListOf<TextInfo>()
        textInfos = list
        for (i in 0 until textNum) {
            list.add(TextInfo(i))
        }
        textPaint.isAntiAlias = true
        textPaint.color = Color.parseColor("#777777")
        textPaint.textSize = KeyBoardUtils.dp2px(context, 16f)

        boxPaint.isAntiAlias = true
        boxPaint.style = Paint.Style.STROKE
        boxPaint.color = Color.parseColor("#dddddd")
        boxPaint.strokeWidth = KeyBoardUtils.dp2px(context, 1f)
        setBackgroundColor(Color.WHITE)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (width - paddingLeft - paddingRight) / textNum
        +paddingTop + paddingBottom
        setMeasuredDimension(width, (height + 0.5f).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val preH = height - paddingTop - paddingBottom
        boxPaint.strokeWidth = KeyBoardUtils.dp2px(context, 1f)
        canvas.drawRect(
            paddingLeft.toFloat(),
            paddingTop.toFloat(),
            (width - paddingRight).toFloat(),
            (height - paddingBottom).toFloat(),
            boxPaint
        )
        boxPaint.strokeWidth = KeyBoardUtils.dp2px(context, 0.5f)
        for (i in 0 until textNum) {
            val info = textInfos[i]
            info.rectF.left = (paddingLeft + i * preH).toFloat()
            info.rectF.right = info.rectF.left + preH
            info.rectF.top = paddingTop.toFloat()
            info.rectF.bottom = info.rectF.top + preH
            if (i != 0) {
                canvas.drawLine(info.rectF.left, info.rectF.top, info.rectF.left, info.rectF.bottom, boxPaint)
            }
            if (!info.text.isBlank()) {
                canvas.drawCircle(
                    info.rectF.left + preH / 2,
                    info.rectF.top + preH / 2,
                    KeyBoardUtils.dp2px(context, 5f),
                    textPaint
                )
            }
        }

    }

    fun appendText(txt: String) {
        if (showText.length == textInfos.size) return
        showText.append(txt)
        showText.forEachIndexed { index, c ->
            textInfos[index].text = c.toString()
        }
        postInvalidate()
        if (showText.length == textInfos.size) {
            verifyCallBack?.invoke(showText.toString())
        }
    }

    fun deleteText() {
        if (!showText.isBlank()) {
            showText.delete(showText.length - 1, showText.length)
            for (i in showText.length until textInfos.size) {
                textInfos[i].text = ""
            }
            postInvalidate()
        }
    }

    fun deleteAll() {
        if (!showText.isBlank()) {
            for (i in 0 until showText.length) {
                showText.delete(i, i + 1)
                textInfos[i].text = ""
            }
            postInvalidate()
        }
    }

    class TextInfo(var index: Int) {
        val rectF = RectF()
        var text: String = ""
    }
}