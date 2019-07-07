package com.heng.keyboard

import android.content.res.ColorStateList
import android.graphics.RectF

/**
 * 键盘 数据
 */
class HKeyBoardData {
    /**
     * 键盘背景
     */
    var backgroundColor: Int? = null
    /**
     * 键背景
     */
    var keyBackgroundColor: ColorStateList? = null
    /**
     * 键文字大小
     */
    var keyTextSize: Float? = null
    /**
     * 键文字颜色
     */
    var keyTextColor: ColorStateList? = null
    /**
     * 键的圆角大小
     */
    var keyBorderRadius: Float=0f
    /**
     * 键盘padding值
     */
    var paddingLeft = 0f
    var paddingTop = 0f
    var paddingRight = 0f
    var paddingBottom = 0f
    /**
     * 键盘宽高比
     */
    var ratioWH = 0.52f //键盘宽高比
    /**
     * 键 列的间距
     */
    var horizontalSpacing: Float? = null
    /**
     * 键 行的间距
     */
    var verticalSpacing: Float? = null

    /**
     * 键盘数据
     */
    var keyBoardRows = mutableListOf<HKeyBoardRow>()
}

/**
 * 每行 数据
 */
class HKeyBoardRow(var rowNum: Int) {
    /**
     * 左侧边距
     */
    var leftSpaceWeight: Float = 0f
    var rightSpaceWeight: Float = 0f
    var keyDataList: MutableList<HKeyData> = mutableListOf()

}

/**
 * 每个键数据
 */
class HKeyData(var rowNum: Int) {
    /**
     * 键 唯一标识
     */
    var code: Int = 0
    /**
     * 键显示的文字
     */
    var value: String? = null
    /**
     * 展示的图片资源  有图片时，则不展示文字
     */
    var drawableRes: Int = 0
    /**
     * 键宽度的权重值
     */
    var weight: Float = 1f
    /**
     * 键占的矩形位置
     */
    var boundRectF: RectF = RectF()
    /**
     * 键背景
     */
    var keyBackgroundColor: ColorStateList? = null
    /**
     * 键文字大小
     */
    var keyTextSize: Float? = null
    /**
     * 键文字颜色
     */
    var keyTextColor: ColorStateList? = null
    /**
     * 键的圆角大小
     */
    var keyBorderRadius: Float?=null

}