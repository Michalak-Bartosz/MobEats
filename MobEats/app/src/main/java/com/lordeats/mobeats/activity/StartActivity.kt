package com.lordeats.mobeats.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.LocaleHelper
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityStartBinding
import com.lordeats.mobeats.events.MessageEvent
import com.lordeats.mobeats.events.RegisterReplyEvent
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.*


class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var client: StompClient

    private lateinit var registerReplyMessage: RegisterReplyEvent

    private var registerReplyTmp: String = ""
    private lateinit var registerReply: JSONObject;
    private var loginReplyTmp: String = ""
    private lateinit var loginReply: JSONObject;

    private lateinit var userData: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)
        changeLngButtonListenerConfig()
        changeModeButtonListenerConfig()
        connectToServer()
        clientLifecycleConfig()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(client.isConnected){
            client.disconnect()
        }
    }

    private fun connectToServer() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/app")
        client.connect()
    }

    @SuppressLint("CheckResult")
    private fun clientLifecycleConfig(){
        client.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> Log.d(ContentValues.TAG, "Stomp connection opened")
                LifecycleEvent.Type.ERROR -> {
                    Log.e(ContentValues.TAG, "Error", lifecycleEvent.exception)
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() }
                    connectToServer()
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d(ContentValues.TAG, "Stomp connection closed")
                }
                else -> Log.d(ContentValues.TAG, "Stomp connection none Error")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent){
        if(client.isConnected) {
            if(event.message!!.getString("type")  == "register") {
                event.message!!.remove("type")
                setOnRegisterSubscribe()
                client.send("/mobEats/signUp", event.message.toString()).subscribe()
            } else if(event.message!!.getString("type") == "login") {
                event.message!!.remove("type")
                userData = event.message!!.toString()
                setLoginSubscribe()
                client.send("/mobEats/signIn", event.message.toString()).subscribe()
            }
        } else {
            DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show()
            connectToServer()
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnRegisterSubscribe() {
        client.topic("/user/queue/register").subscribe { topicMessage ->
            registerReplyTmp = topicMessage.payload
            registerReply = JSONObject(registerReplyTmp)
            when {
                registerReply.getString("value") == "accept" -> {
                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.successfulRegister)).show() }
                    registerReplyMessage = RegisterReplyEvent("accept")
                    EventBus.getDefault().post(registerReplyMessage)
                }
                registerReply.getString("value") == "reject" -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.failToRegister)).show() }
                }
                else -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.noneError)).show() }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setLoginSubscribe() {
        client.topic("/user/queue/login").subscribe { topicMessage ->
            loginReplyTmp = topicMessage.payload
            loginReply = JSONObject(loginReplyTmp)
            when {
                loginReply.getString("value") == "accept" -> {
                    goToMainAppActivity()
                }
                loginReply.getString("value") == "reject" -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.nicknameAndPasswordNotCorrect)).show() }
                }
                else -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.noneError)).show() }
                }
            }
        }
    }

    private fun goToMainAppActivity(){
        intent = Intent(this, AppActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, userData)
        intent.type = "text/plain"
        startActivity(intent)
        this.finish()
    }

    private fun changeLngButtonListenerConfig() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()

        binding.changeLngButton.setOnClickListener {
            when {
                LocaleHelper.getLanguage(this) == "en" -> {
                    LocaleHelper.setLocale(this, "pl")
                    binding.changeLngButton.text = getString(R.string.additionalLng)
                }
                LocaleHelper.getLanguage(this) == "pl" -> {
                    LocaleHelper.setLocale(this, "en")
                    binding.changeLngButton.text = getString(R.string.additionalLng)
                }
                else -> {
                    LocaleHelper.setLocale(this, "pl")
                    binding.changeLngButton.text = getString(R.string.additionalLng)
                }
            }
        }
    }

    private fun changeModeButtonListenerConfig() {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.changeModeButton.setImageResource(R.drawable.ic_light_mode)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.changeModeButton.setImageResource(R.drawable.ic_dark_mode)
        }

        binding.changeModeButton.setOnClickListener {

            if (isNightModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()
                binding.changeModeButton.setImageResource(R.drawable.ic_dark_mode)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()
                binding.changeModeButton.setImageResource(R.drawable.ic_light_mode)
            }

        }
    }

}