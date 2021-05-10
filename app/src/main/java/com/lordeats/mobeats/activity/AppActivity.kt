package com.lordeats.mobeats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

    }
}