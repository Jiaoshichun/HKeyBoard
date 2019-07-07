package com.heng.keyboard.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.heng.keyboard.HKeyBoardHelper
import com.heng.keyboard.HKeyBoardView
import com.heng.keyboard.HKeyData

class AliPayActivity : AppCompatActivity() {
    private lateinit var boardHelper: HKeyBoardHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ali_payctivity)
        title = "仿支付宝支付键盘"
        val editText = findViewById<EditText>(R.id.edt)
        val imgCancel = findViewById<View>(R.id.img_cancel)
        imgCancel.setOnClickListener {
            if (it.visibility == View.VISIBLE)
                editText.setText("")
        }
        boardHelper = HKeyBoardHelper(this)
            .bindEditText(editText)
            .updateKeyBoardStyle(R.xml.abc_keyboard)
            .setOnKeyClickListener(onKeyClickListener)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                imgCancel.visibility = if (s.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    /**
     * 设置特殊按键监听
     */
    private val onKeyClickListener: (code: Int, keyData: HKeyData) -> Boolean = { code, keyData ->
        when (code) {
            HKeyBoardView.ALI_CODE_DONE -> {
                Toast.makeText(this, "完成按钮", Toast.LENGTH_SHORT).show()
                true
            }
            HKeyBoardView.ALI_CODE_ABC_KEYBOARD_NUMBER -> {//切换键盘为 字符1类型
                boardHelper.updateKeyBoardStyle(R.xml.abc_keyboard_symbol1)
                true
            }
            HKeyBoardView.ALI_CODE_SYMBOL1_KEYBOARD_ABC -> {  //切换键盘为 字母键盘
                boardHelper.updateKeyBoardStyle(R.xml.abc_keyboard)
                true
            }
            HKeyBoardView.ALI_CODE_SYMBOL1_KEYBOARD_NUMBER -> {//  切换键盘为 字符2类型
                boardHelper.updateKeyBoardStyle(R.xml.abc_keyboard_symbol2)
                true
            }
            HKeyBoardView.ALI_CODE_SYMBOL2_KEYBOARD_SYMBOL -> {//切换键盘为 字符1类型
                boardHelper.updateKeyBoardStyle(R.xml.abc_keyboard_symbol1)
                true
            }
            else -> false
        }
    }
}
