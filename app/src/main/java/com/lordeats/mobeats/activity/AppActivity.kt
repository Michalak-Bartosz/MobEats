package com.lordeats.mobeats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityAppBinding
import com.lordeats.mobeats.registerLogin.EXTRA_MESSAGE

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        var message = intent.getStringExtra(EXTRA_MESSAGE)
        binding.message.apply {
            text = message
        }
    }
}