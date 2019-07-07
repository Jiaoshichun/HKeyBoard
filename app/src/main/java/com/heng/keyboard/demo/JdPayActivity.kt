package com.heng.keyboard.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.heng.keyboard.HKeyBoardHelper
import com.heng.keyboard.HKeyBoardView

/**
 * 仿京东 输入支付密码
 */
class JdPayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jd_pay)
        val boxEditView = findViewById<JdEditView>(R.id.edt_password)
        setTitle("仿京东输入支付密码")
        boxEditView.verifyCallBack = {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        HKeyBoardHelper(this)
            .bindEditText(boxEditView)
            .updateKeyBoardStyle(R.xml.number_keyboard)
            .setOnKeyClickListener { code, keyData ->
                if (!keyData.value.isNullOrBlank()) {
                    boxEditView.appendText(keyData.value!!)
                } else if (code == HKeyBoardView.CODE_DELETE) {
                    boxEditView.deleteText()
                }
                true
            }.showKeyBoardView(boxEditView)
    }
}
