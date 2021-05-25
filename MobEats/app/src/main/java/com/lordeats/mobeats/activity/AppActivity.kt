package com.lordeats.mobeats.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityAppBinding
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
class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding

    private lateinit var client: StompClient
    private var apiUrl: String = "https://web-eats-server.herokuapp.com/app"

    private lateinit var userDataTmp: String
    private lateinit var userData: JSONObject
    private lateinit var userDataChange: JSONObject

    private lateinit var nickname: String
    private lateinit var password: String

    private var replyDataTmp: String = ""
    private lateinit var replayData: JSONObject

    private lateinit var restaurantList: List<JSONObject>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app)
        changeLngButtonListenerConfig()
        changeModeButtonListenerConfig()
        getUserData()
        connectToServer()
        clientLifecycleConfig()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(client.isConnected){
            client.disconnect()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun getUserData() {
        userDataTmp = intent.getStringExtra("User Data").toString()
        userData = JSONObject(userDataTmp)
        setUserData()
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        binding.greetingText.text = getString(R.string.greetingText) + " " + userData.getString("nickname") + "!"
    }

    @SuppressLint("CheckResult")
    private fun connectToServer() {
//        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/app")
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, apiUrl)
        client.connect()
        client.send("/mobEats/signIn", userDataTmp).subscribe({ },
            { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
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
    fun onMessageEvent(event: MessageEvent) {
        userDataChange = JSONObject()
        if(client.isConnected && event.message!!.getString("type")  == "changeData") {
            when {
                event.message!!.getString("data") == "nickname" -> {
                    if(userData.getString("nickname") != event.message!!.getString("nickname")){
                        nickname = event.message!!.getString("nickname")
                        userDataChange.put("nickname", userData.getString("nickname"))
                        userDataChange.put("type", "nickname")
                        userDataChange.put("newNickname", nickname)
                    } else {
                        DynamicToast.makeError(this, getString(R.string.changeRejectedSameData)).show()
                        return
                    }
                }
                event.message!!.getString("data") == "password" -> {
                    if(userData.getString("password") != event.message!!.getString("password")){
                        password = event.message!!.getString("password")
                        userDataChange.put("nickname", userData.getString("nickname"))
                        userDataChange.put("type", "password")
                        userDataChange.put("newPassword", password)
                    } else {
                        DynamicToast.makeError(this, getString(R.string.changeRejectedSameData)).show()
                        return
                    }
                }
                else -> {
                    DynamicToast.makeError(this, getString(R.string.changeRejected)).show()
                    return
                }
            }
            setOnChangeUserDataSubscribe()
            client.send("/mobEats/changeUserData", userDataChange.toString()).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type")  == "deleteAccount") {
            setOnDeleteAccountSubscribe()
            client.send("/mobEats/deleteAccount", userData.toString()).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type") == "getRestaurantsList") {
            setOnGetRestaurantsListSubscribe()
            client.send("/mobEats/getReservations", userDataChange.getString("nickname")).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else {
            DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show()
            connectToServer()
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnChangeUserDataSubscribe() {
        client.topic("/user/queue/changeData").subscribe { topicMessage ->
            replyDataTmp = topicMessage.payload
            replayData = JSONObject(replyDataTmp)
            when {
                replayData.getString("value") == "acceptNickname" -> {

                    userData.remove("nickname")
                    userData.put("nickname", userDataChange.getString("newNickname"))
                    client.send("/mobEats/signIn", userData.toString()).subscribe({ },
                        { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })

                    this.runOnUiThread { setUserData()
                        DynamicToast.makeSuccess(this, getString(R.string.changeNicknameAccepted)).show() }
                }
                replayData.getString("value") == "acceptPassword" -> {

                    userData.remove("password")
                    userData.put("password", userDataChange.getString("newPassword"))
                    client.send("/mobEats/signIn", userData.toString()).subscribe({ },
                        { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })

                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.changePasswordAccepted)).show() }
                }
                replayData.getString("value") == "reject" -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.changeRejectedNickname)).show() }
                }
                else -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.changeRejected)).show() }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnDeleteAccountSubscribe() {
        client.topic("/user/queue/dellAccount").subscribe { topicMessage ->
            replyDataTmp = topicMessage.payload
            replayData = JSONObject(replyDataTmp)
            when {
                replayData.getString("value") == "accept" -> {
                    client.disconnect()
                    goToStartAppActivity()
                    this.runOnUiThread { setUserData()
                        DynamicToast.makeSuccess(this, getString(R.string.deleteAccountAccepted)).show() }
                }
                replayData.getString("value") == "reject" -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.deleteAccountRejected)).show() }
                }
                else -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.deleteAccountRejected)).show() }
                }
            }
        }
    }

    private fun goToStartAppActivity(){
        intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    @SuppressLint("CheckResult")
    private fun setOnGetRestaurantsListSubscribe() {
        client.topic("/user/queue/getReservationsList").subscribe { topicMessage ->
            replyDataTmp = topicMessage.payload
            replayData = JSONObject(replyDataTmp)
            when {
                replayData.has("value") -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.deleteAccountRejected)).show() }
                }
                else -> {
                    replayData.put("type", "acceptListRestaurants")
                    EventBus.getDefault().post(MessageReplyEvent(replayData))
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.deleteAccountRejected)).show() }
                }
            }

        }
    }

    private fun changeLngButtonListenerConfig() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()

        binding.changeLngAppButton.setOnClickListener {
            when {
                binding.changeLngAppButton.text == "PL" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "pl")
                    updateResources(this, "pl")
                    binding.changeLngAppButton.text = getString(R.string.additionalLng)
                }
                binding.changeLngAppButton.text == "ENG" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "en")
                    updateResources(this, "en")
                    binding.changeLngAppButton.text = getString(R.string.additionalLng)
                }
                else -> { }
            }

            sharedPrefsEdit.apply()
        }
    }

    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun changeModeButtonListenerConfig() {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.changeModeApptButton.setImageResource(R.drawable.ic_light_mode)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.changeModeApptButton.setImageResource(R.drawable.ic_dark_mode)
        }

        binding.changeModeApptButton.setOnClickListener {

            if (isNightModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                binding.changeModeApptButton.setImageResource(R.drawable.ic_dark_mode)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                binding.changeModeApptButton.setImageResource(R.drawable.ic_light_mode)
            }
            sharedPrefsEdit.apply()
        }
    }

}
