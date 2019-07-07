package com.heng.keyboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.support.annotation.XmlRes
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.TextView
import kotlin.math.min

/**
 * 自定义键盘
 */
class HKeyBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnTouchListener {
    companion object {
        const val CODE_DELETE = -5
        const val CODE_BLACK = 38
        const val CODE_SHIFT = -1 //上档键 切换大小写
        val SPECIAL_CODES = hashSetOf(CODE_DELETE, CODE_BLACK, CODE_SHIFT)

        //阿里键盘的完成按钮
        const val ALI_CODE_DONE = -10
        //阿里字符键盘的数字按钮
        const val ALI_CODE_ABC_KEYBOARD_NUMBER = -11
        //阿里字符键盘 符号1模式 中的数组按钮
        const val ALI_CODE_SYMBOL1_KEYBOARD_NUMBER = -12
        //阿里字符键盘 符号1模式 中的字母按钮
        const val ALI_CODE_SYMBOL1_KEYBOARD_ABC = -13
        //阿里字符键盘 符号2模式 中的字符按钮
        const val ALI_CODE_SYMBOL2_KEYBOARD_SYMBOL = -14
    }

    //长按事件
    private val HANDLER_LONG_CLICK = 1001
    private val TAG = "HKeyBoardView"
    //当前是否是大写
    private var isUpperCase = false
    private var keyBoardData: HKeyBoardData = HKeyBoardParser.parser(context, R.xml.number_keyboard)

    /***文字画笔*/
    private val textPaint = Paint()
    /***背景画笔*/
    private val backgroundPaint = Paint()

    /**是否计算完成每个键的位置，避免重复计算*/
    private var isComputePosOver = false
    /**
     * 当前绑定的 editText
     */
    var bindEditText: View? = null

    /**
     * 按键的点击事件，可以处理自定义事件（注意：长按时 会多次执行）
     */
    internal var onKeyClickListener: ((code: Int, keyData: HKeyData) -> Boolean)? = null

    private val handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HANDLER_LONG_CLICK -> {//处理长按事件，长按时 一直执行点击事件
                    if (currentTouchKeyData != null) {
                        onKeyClick(currentTouchKeyData!!)
                        sendEmptyMessageDelayed(HANDLER_LONG_CLICK, 100)
                    }
                }
            }
        }

    }

    init {
        textPaint.isAntiAlias = true
        textPaint.color = context.resources.getColor(R.color.hKeyBoardTextColor)
        textPaint.textSize = KeyBoardUtils.dp2px(context, 20f)


        backgroundPaint.isAntiAlias = true
        textPaint.color = Color.WHITE
        setKeyBoardData(keyBoardData)
        setOnTouchListener(this)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            KeyBoardUtils.getDisplayWidth(context),
            getKeyBoardHeight()
        )
    }

    fun getKeyBoardHeight() = (KeyBoardUtils.getDisplayWidth(context) * keyBoardData.ratioWH + 0.5f).toInt()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //计算键的位置
        computeKeyPos()
        //绘制键
        drawKey(canvas)
        postInvalidate()
    }

    /**
     * 当前正在被按下的键
     */
    private var currentTouchKeyData: HKeyData? = null

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (currentTouchKeyData != null) {
                    onKeyClick(currentTouchKeyData!!)
                }
                currentTouchKeyData = null
                invalidate()
                handler.removeMessages(HANDLER_LONG_CLICK)
            }
            MotionEvent.ACTION_CANCEL -> {
                currentTouchKeyData = null
                invalidate()
                handler.removeMessages(HANDLER_LONG_CLICK)
            }
            MotionEvent.ACTION_DOWN -> {
                keyBoardData.keyBoardRows.forEach { boardRow ->
                    boardRow.keyDataList.forEach {
                        if (it.boundRectF.contains(event.x, event.y)) {
                            currentTouchKeyData = it
                            invalidate()
                            handler.sendEmptyMessageDelayed(
                                HANDLER_LONG_CLICK,
                                ViewConfiguration.getLongPressTimeout().toLong()
                            )
                            return true
                        }

                    }
                }
            }
        }
        return true
    }

    /**
     * 处理按钮点击事件
     */
    private fun onKeyClick(keyData: HKeyData) {
        if (onKeyClickListener?.invoke(keyData.code, keyData) == true) return
        if (bindEditText !is TextView) return
        val textView = bindEditText as TextView
        val start = textView.selectionStart
        val end = textView.selectionEnd
        val stringBuilder = textView.editableText as SpannableStringBuilder?

        if (keyData.value.isNullOrBlank() || SPECIAL_CODES.contains(keyData.code)) {
            when (keyData.code) {
                CODE_BLACK -> stringBuilder?.replace(
                    start,
                    end,
                    " "
                )
                CODE_DELETE -> {
                    if (start != end)
                        stringBuilder?.replace(
                            start,
                            end,
                            ""
                        )
                    else if (start != 0) {
                        stringBuilder?.delete(start - 1, start)
                    }
                }
                CODE_SHIFT -> {
                    //切换大小写状态
                    isUpperCase = !isUpperCase
                    keyBoardData.keyBoardRows.forEach { row ->
                        row.keyDataList.forEach {
                            if (!it.value.isNullOrBlank()) {
                                it.value = if (isUpperCase) it.value!!.toUpperCase() else it.value!!.toLowerCase()
                            }
                        }
                    }
                }

            }
        } else {
            stringBuilder?.replace(
                start,
                end,
                keyData.value
            )
        }

    }

    /**
     * 设置键盘数据
     */
    internal fun setKeyBoardData(keyBoardData: HKeyBoardData) {
        isUpperCase = false
        this.keyBoardData = keyBoardData
        val color = keyBoardData.backgroundColor
        if (color ?: 0 > 0) {
            setBackgroundResource(color!!)
        }
        setPadding(
            keyBoardData.paddingLeft.toInt(),
            keyBoardData.paddingTop.toInt(),
            keyBoardData.paddingRight.toInt(),
            keyBoardData.paddingBottom.toInt()
        )
        isComputePosOver = false
        requestLayout()
        postInvalidate()
    }

    /**
     * 设置键盘数据
     */
    internal fun setKeyBoardData(@XmlRes xmlId: Int) {
        setKeyBoardData(HKeyBoardParser.parser(context, xmlId))
    }

    /**
     * 绘制键
     */
    private fun drawKey(canvas: Canvas) {
        keyBoardData.keyBoardRows.forEach { boardRow ->
            boardRow.keyDataList.forEach {
                //键的文字颜色
                val textColor = it.keyTextColor ?: keyBoardData.keyTextColor
                if (textColor != null) {
                    textPaint.color = if (currentTouchKeyData == it) textColor.getColorForState(
                        PRESSED_ENABLED_STATE_SET,
                        textColor.defaultColor
                    ) else if (isUpperCase && it.code == CODE_SHIFT)
                        textColor.getColorForState(SELECTED_STATE_SET, textColor.defaultColor)
                    else textColor.defaultColor
                }
                //文字大小
                textPaint.textSize = it.keyTextSize ?: keyBoardData.keyTextSize ?: textPaint.textSize

                //键背景颜色
                val keyBackgroundColor = it.keyBackgroundColor ?: keyBoardData.keyBackgroundColor
                if (keyBackgroundColor != null) {
                    backgroundPaint.color = if (currentTouchKeyData == it) keyBackgroundColor.getColorForState(
                        PRESSED_ENABLED_STATE_SET,
                        keyBackgroundColor.defaultColor
                    ) else if (isUpperCase && it.code == CODE_SHIFT)
                        keyBackgroundColor.getColorForState(SELECTED_STATE_SET, keyBackgroundColor.defaultColor)
                    else keyBackgroundColor.defaultColor
                }

                //绘制 背景
                canvas.drawRoundRect(
                    it.boundRectF,
                    it.keyBorderRadius ?: keyBoardData.keyBorderRadius,
                    it.keyBorderRadius ?: keyBoardData.keyBorderRadius,
                    backgroundPaint
                )

                val keyWidth = it.boundRectF.right - it.boundRectF.left
                val keyHeight = it.boundRectF.bottom - it.boundRectF.top
                if (it.value.isNullOrBlank()) {
                    if (it.drawableRes > 0) {//绘制 图片
                        val drawable = resources.getDrawable(it.drawableRes, context.theme)
                        if (currentTouchKeyData == it) {
                            drawable.state = PRESSED_ENABLED_STATE_SET
                        } else if (isUpperCase && it.code == CODE_SHIFT) {
                            //如果当前是大写并且该键是shift键，则展示为选中状态
                            drawable.state = SELECTED_STATE_SET
                        }
                        //X轴的间距
                        val xSpace = (keyWidth - min(drawable.minimumWidth.toFloat(), keyWidth)) / 2
                        //y轴的间距
                        val ySpace = (keyHeight - min(drawable.minimumHeight.toFloat(), keyHeight)) / 2
                        if (drawable != null) {
                            drawable.setBounds(
                                (it.boundRectF.left + xSpace).toInt(),
                                (it.boundRectF.top + ySpace).toInt(),
                                (it.boundRectF.right + 0.5f - xSpace).toInt(),
                                (it.boundRectF.bottom + 0.5f - ySpace).toInt()
                            )
                            drawable.draw(canvas)
                        }
                    }
                } else { //绘制文字
                    val x = it.boundRectF.left + keyWidth / 2 -
                            textPaint.measureText(it.value) / 2
                    val y = it.boundRectF.top + keyHeight / 2 -
                            (textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent) / 2 -
                            textPaint.fontMetrics.ascent
                    canvas.drawText(it.value!!, x, y, textPaint)
                }
            }
        }
    }

    /**
     * 计算每个键的位置
     */
    private fun computeKeyPos() {
        if (isComputePosOver) return
        val verticalSpacing = keyBoardData.verticalSpacing ?: KeyBoardUtils.dp2px(context, 0.5f)
        val horizontalSpacing = keyBoardData.horizontalSpacing ?: KeyBoardUtils.dp2px(context, 0.5f)
        //每行高度
        val perHeight =
            (height - paddingTop - paddingBottom - (keyBoardData.keyBoardRows.size - 1) * verticalSpacing) / keyBoardData.keyBoardRows.size

        keyBoardData.keyBoardRows.forEach { boardRow ->
            //行的权重总数
            val weightNum =
                boardRow.keyDataList.sumByDouble { it.weight.toDouble() } + boardRow.leftSpaceWeight + boardRow.rightSpaceWeight
            //行每个权重的宽度
            val perWidth =
                (width - paddingLeft - paddingRight - (weightNum - 1) * horizontalSpacing) / weightNum

            //左侧已占用权重
            var startWeight = boardRow.leftSpaceWeight

            boardRow.keyDataList.forEachIndexed { index, keyData ->
                keyData.boundRectF.left =
                    (paddingLeft + startWeight * perWidth + startWeight * horizontalSpacing).toFloat()
                keyData.boundRectF.top = paddingTop + (perHeight + verticalSpacing) * keyData.rowNum
                keyData.boundRectF.right = if (index == boardRow.keyDataList.size - 1) {
                    width.toFloat() - paddingRight - boardRow.rightSpaceWeight * perWidth.toFloat()
                } else {
                    (keyData.boundRectF.left + perWidth * keyData.weight + (keyData.weight - 1) * horizontalSpacing).toFloat()
                }
                keyData.boundRectF.bottom = keyData.boundRectF.top + perHeight
                startWeight += keyData.weight

            }
        }
        isComputePosOver = true
    }


}