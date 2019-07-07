package com.heng.keyboard

import android.content.Context
import android.content.res.XmlResourceParser
import android.support.annotation.XmlRes

object HKeyBoardParser {
    const val HKEYBOARD = "HKeyboard"
    const val KEYBOARD_ROW = "Row"
    const val KEYBOARD_KEY = "Key"
    const val KEY_CODE = "code"
    const val KEY_VALUE = "value"
    const val KEY_DRAWABLE_RES = "drawableRes"
    const val KEY_WEIGHT = "weight"
    //行左侧空隙 权重
    const val ROW_LEFT_SPACE_WEIGHT = "leftSpaceWeight"
    //行右侧空隙 权重
    const val ROW_RIGHT_SPACE_WEIGHT = "rightSpaceWeight"
    //键盘宽高比
    const val KEYBORAD_RATIO_WH = "ratioWH"
    //键每列的 间距
    const val KEYBORAD_HORIZONTALSPACING = "horizontalSpacing"
    //键每行的间距
    const val KEYBORAD_VERTICALSPACING = "verticalSpacing"
    //键盘的背景
    const val KEYBORAD_BACKGROUND_COLOR = "backgroundColor"
    //圆角大小
    const val KEY_BORDER_RADIUS = "keyBorderRadius"
    //键盘的padding值
    const val KEYBORAD_PADDINGLEFT = "paddingLeft"
    const val KEYBORAD_PADDINGRIGHT = "paddingRight"
    const val KEYBORAD_PADDINGTOP = "paddingTop"
    const val KEYBORAD_PADDINGBOTTOM = "paddingBottom"
    //键盘的文字尺寸
    const val KEY_TEXTSIZE = "keyTextSize"
    //键盘的文字颜色
    const val KEY_TEXTCOLOR = "keyTextColor"
    //每个键的背景
    const val KEY_BACKGROUND_COLOR = "keyBackgroundColor"


    fun parser(context: Context, @XmlRes xmlId: Int): HKeyBoardData {
        val resourceParser = context.resources.getXml(xmlId)
        var next = resourceParser.next()
        var rowNum = -1
        val boardData = HKeyBoardData()
        while (next != XmlResourceParser.END_DOCUMENT) {
            if (next == XmlResourceParser.START_TAG) {
                when (resourceParser.name) {
                    HKEYBOARD -> {
                        parserKeyBoardData(resourceParser, boardData, context)
                    }
                    KEYBOARD_ROW -> {
                        rowNum++
                        val boardRow = HKeyBoardRow(rowNum)
                        boardData.keyBoardRows.add(boardRow)
                        parserRows(resourceParser, boardRow)
                    }
                    KEYBOARD_KEY -> {
                        val element = HKeyData(rowNum)
                        boardData.keyBoardRows[rowNum].keyDataList.add(element)
                        parserKeys(context, resourceParser, element)
                    }
                }

            }
            next = resourceParser.next()
        }
        return boardData
    }

    /**
     * 解析键信息
     */
    private fun parserKeys(context: Context, resourceParser: XmlResourceParser, element: HKeyData) {
        for (index in 0 until resourceParser.attributeCount) {
            when (resourceParser.getAttributeName(index)) {
                KEY_CODE -> element.code = resourceParser.getAttributeIntValue(index, 0)
                KEY_VALUE -> element.value = resourceParser.getAttributeValue(index)
                KEY_DRAWABLE_RES -> element.drawableRes = resourceParser.getAttributeResourceValue(index, 0)
                KEY_WEIGHT -> element.weight = resourceParser.getAttributeFloatValue(index, 1f)
                KEY_TEXTSIZE -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        element.keyTextSize = context.resources.getDimension(resourceValue)
                    }
                }
                KEY_BACKGROUND_COLOR -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        element.keyBackgroundColor = context.resources.getColorStateList(resourceValue)
                    }
                }
                //解析键文字颜色
                KEY_TEXTCOLOR -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        element.keyTextColor = context.resources.getColorStateList(resourceValue)
                    }
                }
                //解析圆角
                KEY_BORDER_RADIUS -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        element.keyBorderRadius = context.resources.getDimension(resourceValue)
                    }
                }
            }
        }
    }

    /**
     * 解析行 信息
     */
    private fun parserRows(
        resourceParser: XmlResourceParser,
        boardRow: HKeyBoardRow
    ) {
        for (index in 0 until resourceParser.attributeCount) {
            when (resourceParser.getAttributeName(index)) {
                ROW_LEFT_SPACE_WEIGHT -> boardRow.leftSpaceWeight =
                    resourceParser.getAttributeFloatValue(index, 0f)
                ROW_RIGHT_SPACE_WEIGHT -> boardRow.rightSpaceWeight =
                    resourceParser.getAttributeFloatValue(index, 0f)
            }
        }
    }

    /**
     * 解析键盘信息
     */
    private fun parserKeyBoardData(
        resourceParser: XmlResourceParser,
        boardData: HKeyBoardData,
        context: Context
    ) {
        for (index in 0 until resourceParser.attributeCount) {
            when (resourceParser.getAttributeName(index)) {
                //解析就键盘背景
                KEYBORAD_BACKGROUND_COLOR -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.backgroundColor = resourceValue
                    }
                }
                //解析键背景
                KEY_BACKGROUND_COLOR -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.keyBackgroundColor = context.resources.getColorStateList(resourceValue)
                    }
                }
                //解析键文字颜色
                KEY_TEXTCOLOR -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.keyTextColor = context.resources.getColorStateList(resourceValue)
                    }
                }
                //解析键文字大小
                KEY_TEXTSIZE -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.keyTextSize = context.resources.getDimension(resourceValue)
                    }
                }
                //解析圆角
                KEY_BORDER_RADIUS -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.keyBorderRadius = context.resources.getDimension(resourceValue)
                    }
                }
                //解析键盘 padding值
                KEYBORAD_PADDINGLEFT -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.paddingLeft = context.resources.getDimension(resourceValue)
                    }
                }
                KEYBORAD_PADDINGRIGHT -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.paddingRight = context.resources.getDimension(resourceValue)
                    }
                }
                KEYBORAD_PADDINGTOP -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.paddingTop = context.resources.getDimension(resourceValue)
                    }
                }
                KEYBORAD_PADDINGBOTTOM -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.paddingBottom = context.resources.getDimension(resourceValue)
                    }
                }
                //解析 列间距
                KEYBORAD_HORIZONTALSPACING -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.horizontalSpacing = context.resources.getDimension(resourceValue)
                    }
                }
                //解析行间距
                KEYBORAD_VERTICALSPACING -> {
                    val resourceValue = resourceParser.getAttributeResourceValue(index, 0)
                    if (resourceValue > 0) {
                        boardData.verticalSpacing = context.resources.getDimension(resourceValue)
                    }
                }
                //解析键盘宽高比
                KEYBORAD_RATIO_WH -> {
                    boardData.ratioWH = resourceParser.getAttributeFloatValue(index, 0.52f)
                }
            }
        }
    }

}

