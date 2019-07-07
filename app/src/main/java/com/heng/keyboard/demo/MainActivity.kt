package com.heng.keyboard.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_jd).setOnClickListener {
            startActivity(Intent(this, JdPayActivity::class.java))
        }
        findViewById<Button>(R.id.btn_ali).setOnClickListener {
            startActivity(Intent(this, AliPayActivity::class.java))
        }
    }
}
