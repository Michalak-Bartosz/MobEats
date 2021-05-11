package com.lordeats.mobeats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityAppBinding
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompMessage

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }
}