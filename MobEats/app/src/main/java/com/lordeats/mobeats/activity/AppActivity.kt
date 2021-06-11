package com.lordeats.mobeats.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding

    private lateinit var client: StompClient
    private var apiUrl: String = "https://web-eats-server.herokuapp.com/app"

    private lateinit var userDataTmp: String
    private lateinit var userData: JSONObject
    private lateinit var userDataChange: JSONObject

    private lateinit var restaurantDataTmp: String
    private lateinit var restaurantData: JSONObject

    private lateinit var findPplDataTmp: String
    private lateinit var findPplData: JSONObject

    private lateinit var nickname: String
    private lateinit var password: String

    private var replyDataTmp: String = ""
    private lateinit var replyData: JSONObject

    private lateinit var messageToSend: MessageEvent

    private val notificationIdList: HashMap<String, Int> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app)
        changeLngButtonListenerConfig()
        changeModeButtonListenerConfig()
        getUserData()
        connectToServer()
        clientLifecycleConfig()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(client.isConnected){
            client.disconnect()
        }
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().hasSubscriberForEvent(MessageEvent::class.java)){
            EventBus.getDefault().register(this)
        }
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

    private fun createNotificationChannel(nickname: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.findPplTitle)
            val descriptionText = getString(R.string.findPplText)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(nickname, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(userDataFindPpl: JSONObject) {
        val notificationIntent = Intent(this, MapsActivity::class.java)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        notificationIntent.action = Intent.ACTION_SEND
        Log.d("BARTEK USER DATA DO WYSLANIA", userDataFindPpl.toString())
        notificationIntent.putExtra("USER_DATA_FIND_PPL", userDataFindPpl.toString())
        notificationIntent.type = "text/plain"

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nickname: String = userDataFindPpl.getString("nickname")
        if(!notificationIdList.contains(nickname)) {
            createNotificationChannel(nickname)
            notificationIdList[nickname] = notificationIdList.count()
        }

        val builder =NotificationCompat.Builder(this, nickname)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setContentTitle(getString(R.string.findPplTitle))
            .setContentText(getString(R.string.findPplText))
            .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.findPplStarText) + " " + nickname + " " + getString(R.string.findPplEndText)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationIdList.getValue(nickname), builder.build()) }
    }

    @SuppressLint("CheckResult")
    private fun connectToServer() {
//        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/app")
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, apiUrl)
        client.connect()
        client.send("/mobEats/signIn", userDataTmp).subscribe({ },
            { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        setOnFindPll()
        setOnChangeUserDataSubscribe()
        setOnDeleteAccountSubscribe()
        setOnGetRestaurantsListSubscribe()
        setOnDeleteReservationSubscribe()
        setOnAddReservationSubscribe()
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
            client.send("/mobEats/changeUserData", userDataChange.toString()).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type")  == "deleteAccount") {
            client.send("/mobEats/deleteAccount", userData.toString()).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type") == "getRestaurantsList") {
            client.send("/mobEats/getReservations", userData.getString("nickname")).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type") == "removeRestaurant") {
            client.send("/mobEats/deleteReservation", event.message!!.getString("value")).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type") == "addRestaurant") {
            restaurantDataTmp = event.message!!.toString()
            restaurantData = JSONObject(restaurantDataTmp)
            restaurantData.remove("type")
            restaurantData.put("nickname", userData.getString("nickname"))
            client.send("/mobEats/addReservation", restaurantData.toString()).subscribe({ },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if(client.isConnected && event.message!!.getString("type")  == "findPpl") {
            findPplDataTmp = event.message!!.toString()
            findPplData = JSONObject(findPplDataTmp)
            findPplData.remove("type")
            findPplData.put("nickname", userData.getString("nickname"))
            client.send("/mobEats/findPpl", findPplData.toString()).subscribe({ this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.findPplSuccess)).show() } },
                { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })
        } else if (client.isConnected && event.message!!.getString("type")  == "findPplReply") { } else {
            DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show()
            connectToServer()
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnFindPll() {
        client.topic("/topic/messages").subscribe() { topicMessage ->
            replyDataTmp = topicMessage.payload
            replyData = JSONObject(replyDataTmp)
            if(replyData.getString("nickname") != userData.getString("nickname")) {
                replyData.put("type", "findPplReply")
                sendNotification(replyData)
                messageToSend = MessageEvent(replyData)
                EventBus.getDefault().post(messageToSend)
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnChangeUserDataSubscribe() {
        client.topic("/user/queue/changeData").subscribe { topicMessage ->
            replyDataTmp = topicMessage.payload
            replyData = JSONObject(replyDataTmp)
            when {
                replyData.getString("value") == "acceptNickname" -> {

                    userData.remove("nickname")
                    userData.put("nickname", userDataChange.getString("newNickname"))
                    client.send("/mobEats/signIn", userData.toString()).subscribe({ },
                        { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })

                    this.runOnUiThread { setUserData()
                        DynamicToast.makeSuccess(this, getString(R.string.changeNicknameAccepted)).show() }
                }
                replyData.getString("value") == "acceptPassword" -> {

                    userData.remove("password")
                    userData.put("password", userDataChange.getString("newPassword"))
                    client.send("/mobEats/signIn", userData.toString()).subscribe({ },
                        { this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.serverConnectionError)).show() } })

                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.changePasswordAccepted)).show() }
                }
                replyData.getString("value") == "reject" -> {
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
            replyData = JSONObject(replyDataTmp)
            when {
                replyData.getString("value") == "accept" -> {
                    client.disconnect()
                    goToStartAppActivity()
                    this.runOnUiThread { setUserData()
                        DynamicToast.makeSuccess(this, getString(R.string.deleteAccountAccepted)).show() }
                }
                replyData.getString("value") == "reject" -> {
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
            when {
                replyDataTmp.isEmpty() -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.failGetRestaurantList)).show() }
                }
                else -> {
                    EventBus.getDefault().post(MessageReplyEvent(replyDataTmp))
                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.succeedGetRestaurantList)).show() }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnDeleteReservationSubscribe() {
        client.topic("/user/queue/dellReservation").subscribe { topicMessage ->
            replyDataTmp = topicMessage.payload
            replyData = JSONObject(replyDataTmp)
            when {
                replyData.getString("value") == "accept" -> {
                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.deleteRestaurantAccepted)).show() }
                }
                replyData.getString("value") == "reject" -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.deleteRestaurantRejected)).show() }
                }
                else -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.deleteRestaurantRejected)).show() }
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnAddReservationSubscribe() {
        client.topic("/user/queue/addNewReservation").subscribe { topicMessage ->
            replyDataTmp = topicMessage.payload
            replyData = JSONObject(replyDataTmp)
            when {
                replyData.getString("value") == "accept" -> {
                    this.runOnUiThread { DynamicToast.makeSuccess(this, getString(R.string.addRestaurantAccepted)).show() }
                }
                replyData.getString("value") == "reject" -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.addRestaurantRejected)).show() }
                }
                else -> {
                    this.runOnUiThread { DynamicToast.makeError(this, getString(R.string.addRestaurantRejected)).show() }
                }
            }
        }
    }

    private fun changeLngButtonListenerConfig() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()

        binding.changeLngAppButton.setOnClickListener {
            when (binding.changeLngAppButton.text) {
                "PL" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "pl")
                    updateResources("pl")
                    binding.changeLngAppButton.text = getString(R.string.additionalLng)
                }
                "ENG" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "en")
                    updateResources("en")
                    binding.changeLngAppButton.text = getString(R.string.additionalLng)
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
            binding.changeModeAppButton.setImageResource(R.drawable.ic_light_mode)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.changeModeAppButton.setImageResource(R.drawable.ic_dark_mode)
        }

        binding.changeModeAppButton.setOnClickListener {

            if (isNightModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                binding.changeModeAppButton.setImageResource(R.drawable.ic_dark_mode)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                binding.changeModeAppButton.setImageResource(R.drawable.ic_light_mode)
            }
            sharedPrefsEdit.apply()
        }
    }

}
