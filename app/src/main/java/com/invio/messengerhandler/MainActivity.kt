package com.invio.messengerhandler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.invio.messengerhandler.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.rand = (application as MessengerHandlerApplication).randomNumber

        binding.btnGoToAnotherProcess.setOnClickListener {
            startActivity(Intent(this, AnotherProcessActivity::class.java))
        }
    }
}