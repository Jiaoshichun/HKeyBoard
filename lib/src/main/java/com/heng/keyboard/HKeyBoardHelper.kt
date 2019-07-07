package com.heng.keyboard

import android.app.Activity
import android.support.annotation.XmlRes
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView


class HKeyBoardHelper(activity: Activity) {
    private val contentView: FrameLayout = activity.findViewById(android.R.id.content)
    private var boardView: HKeyBoardView = HKeyBoardView(activity)

    companion object {
        private const val HAS_BIND_TAG = "HKeyBoardHelper"
    }

    init {
        //添加键盘布局
        boardView.visibility = View.GONE
        boardView.tag = boardView
        contentView.addView(
            boardView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
        )

    }


    fun bindEditText(editText: View): HKeyBoardHelper {
        editText.tag = HAS_BIND_TAG
        //隐藏系统的键盘
        if (editText is TextView) {
            editText.showSoftInputOnFocus = false
        }
        //设置焦点变化的监听
        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyBoardView(v)
            } else {
                hideKeyBoardView(v)
            }
        }
        //设置点击事件
        editText.setOnClickListener {
            showKeyBoardView(it)
        }
        //拦截返回键
        editText.setOnKeyListener { v, keyCode, event ->
            if (KeyEvent.ACTION_DOWN == event.action) { //拦截返回键 隐藏键盘
                return@setOnKeyListener hideKeyBoardView(v)
            }
            return@setOnKeyListener false
        }
        return this
    }

    /**
     * 按键的点击事件，可以处理自定义事件（注意：长按时 会多次执行）
     */
    fun setOnKeyClickListener(listener: (code: Int, keyData: HKeyData) -> Boolean): HKeyBoardHelper {
        boardView.onKeyClickListener = listener
        return this
    }

    /**
     * 更新键盘样式
     */
    fun updateKeyBoardStyle(@XmlRes xmlId: Int): HKeyBoardHelper {
        boardView.setKeyBoardData(xmlId)
        return this
    }

    /**
     * 更新键盘样式
     */
    fun updateKeyBoardStyle(keyBoardData: HKeyBoardData): HKeyBoardHelper {
        boardView.setKeyBoardData(keyBoardData)
        return this
    }

    /**
     * 隐藏键盘动画
     */
    fun hideKeyBoardView(editText: View): Boolean {
        if (boardView.visibility == View.GONE || boardView.bindEditText != editText) return false
        val animation = AnimationUtils.loadAnimation(boardView.context, R.anim.keyboard_hide)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                boardView.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        boardView.bindEditText = null
        boardView.startAnimation(animation)
        contentView.getChildAt(0).scrollTo(0, 0)
        return true
    }

    /**
     * 展示键盘动画
     */
    fun showKeyBoardView(editText: View) {
        if (editText.tag != HAS_BIND_TAG) {
            bindEditText(editText)
        }
        if (boardView.visibility == View.VISIBLE) return
        boardView.bindEditText = editText
        boardView.visibility = View.VISIBLE
        boardView.startAnimation(AnimationUtils.loadAnimation(boardView.context, R.anim.keyboard_show))

        //避免输入框遮盖输入框
        editText.postDelayed({
            val array = IntArray(2)
            editText.getLocationInWindow(array)
            val height = KeyBoardUtils.getDisplayHeight(editText.context)
            val editTextHeight = editText.height + KeyBoardUtils.dp2px(editText.context, 15f)
            if (height - boardView.getKeyBoardHeight() < array[1] + editTextHeight) {
                contentView.getChildAt(0)
                    .scrollTo(0, ((array[1] + editTextHeight) - (height - boardView.getKeyBoardHeight())).toInt())
            }
        }, if (editText.height == 0) 300L else 200L)


    }


}