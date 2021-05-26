package com.lordeats.mobeats.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityStartBinding
import com.lordeats.mobeats.events.MessageEvent
import com.lordeats.mobeats.events.MessageReplyEvent
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.*


@Suppress("DEPRECATION")
class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private lateinit var client: StompClient
    private var apiUrl: String = "https://web-eats-server.herokuapp.com/app"

    private var registerReplyTmp: String = ""
    private lateinit var registerReply: JSONObject
    private var loginReplyTmp: String = ""
    private lateinit var loginReply: JSONObject

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
//        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, apiUrl)
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

    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent){
        if(client.isConnected) {
            if(event.message!!.getString("type")  == "register") {
                event.message!!.remove("type")
                setOnRegisterSubscribe()
                client.send("/mobEats/signUp", event.message.toString()).subscribe({ },
                    { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
            } else if(event.message!!.getString("type") == "login") {
                event.message!!.remove("type")
                userData = event.message!!.toString()
                setLoginSubscribe()
                client.send("/mobEats/signIn", event.message.toString()).subscribe({ },
                    { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
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
                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.successfulRegister)).show()

                       }
                    EventBus.getDefault().post(MessageReplyEvent("acceptRegister"))
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
                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.successfulLogin)).show() }
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
        client.disconnect()
        intent = Intent(this, AppActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.putExtra("User Data", userData)
        intent.type = "text/plain"
        startActivity(intent)
        this.finish()
    }

    private fun changeLngButtonListenerConfig() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()

        binding.changeLngStartButton.setOnClickListener {
            when (binding.changeLngStartButton.text) {
                "PL" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "pl")
                    updateResources("pl")
                    binding.changeLngStartButton.text = getString(R.string.additionalLng)
                }
                "ENG" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "en")
                    updateResources("en")
                    binding.changeLngStartButton.text = getString(R.string.additionalLng)
                }
                else -> { }
            }
            sharedPrefsEdit.apply()
        }
    }

    private fun updateResources(language: String) {
        val myLocale = Locale(language)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        onConfigurationChanged(conf)
    }

    private fun changeModeButtonListenerConfig() {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.changeModeStartButton.setImageResource(R.drawable.ic_light_mode)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.changeModeStartButton.setImageResource(R.drawable.ic_dark_mode)
        }

        binding.changeModeStartButton.setOnClickListener {

            if (isNightModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                binding.changeModeStartButton.setImageResource(R.drawable.ic_dark_mode)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                binding.changeModeStartButton.setImageResource(R.drawable.ic_light_mode)
            }
            sharedPrefsEdit.apply()
        }
    }

}