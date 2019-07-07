package com.heng.keyboard

import android.content.Context

object KeyBoardUtils {
    const val CODE_DELETE = -5
    fun dp2px(
        context: Context,
        dp: Float
    ): Float {
        val density = context.resources.displayMetrics.density
        return dp * density + 0.5f
    }

    fun px2dp(
        context: Context,
        px: Float
    ): Float {
        val density = context.resources.displayMetrics.density
        return px / density + 0.5f
    }

    fun getDisplayWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getDisplayHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }
}