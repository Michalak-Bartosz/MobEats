package com.lordeats.mobeats.activity

import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityStartBinding
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient


class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var client: StompClient
    private var checkRegister: String = "false"

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

    private fun connectToServer() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/chat")
        client.connect()
        client.topic("/user/queue/register").subscribe { topicMessage ->
            checkRegister = topicMessage.getPayload()
        }

//        client.lifecycle().subscribe {
//            when (it.type) {
//                LifecycleEvent.Type.CLOSED -> connectToServer()
//            }
//        }
    }

    public fun register(nickname: String): Boolean {
        client.send("/mobEats/signUp", nickname).subscribe()
        if(checkRegister == "false"){
            DynamicToast.makeError(this, getString(R.string.failToRegister)).show()
            return false
        }
        DynamicToast.makeSuccess(this, getString(R.string.successfulRegister)).show()
        return true
    }
}