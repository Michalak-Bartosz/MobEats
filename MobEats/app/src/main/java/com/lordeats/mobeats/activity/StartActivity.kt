package com.lordeats.mobeats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityStartBinding
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var client: StompClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)
        connectToServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(client.isConnected){
            client.disconnect()
        }
    }

    override fun onRestart() {
        super.onRestart()
        if(!client.isConnected){
            connectToServer()
        }
    }

    private fun connectToServer(){
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/chat")
        client.connect()
//        client.send("/app/name", "Bartek").subscribe()
//        client.lifecycle().subscribe {
//            when (it.type) {
//                LifecycleEvent.Type.CLOSED -> connectToServer()
//            }
//        }
    }
}