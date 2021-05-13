package com.lordeats.mobeats.registerLogin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentRegisterBinding
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.net.SocketTimeoutException


/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var client: StompClient
    private lateinit var nickname: String
    private lateinit var password: String
    private var registerReplyTmp: String = ""
    private lateinit var registerReply: JSONObject;
    private var registerPayload: JSONObject = JSONObject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerButtonListener()
        connectToServer()
        clientLifecycleConfig()
        return binding.root
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
                    activity?.runOnUiThread { context?.let { DynamicToast.makeError(it, getString(R.string.serverConnectionError)).show() } }
                    connectToServer()
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d(ContentValues.TAG, "Stomp connection closed")
                    connectToServer()
                }
                else -> Log.d(ContentValues.TAG, "Stomp connection none Error")
            }
        }
    }

    private fun checkTextContext(): Boolean {
        val loginCheck: String = binding.nicknameRegisterPlainText.text.toString()
        val passwordOneCheck: String = binding.keyOnePassword.text.toString()
        val passwordTwoCheck: String = binding.keyTwoPassword.text.toString()

        return if(loginCheck.trim().isNotEmpty() && passwordOneCheck.trim().isNotEmpty() && passwordTwoCheck.trim().isNotEmpty()) {
            if(passwordOneCheck == passwordTwoCheck){
                true
            }else{
                context?.let { DynamicToast.makeError(it, getString(R.string.keyPasswordNotEqual)).show() }
                false
            }
        }else{
            context?.let { DynamicToast.makeError(it, getString(R.string.noDataToSingUp)).show() }
            false
        }
    }

    private fun registerButtonListener() {
        binding.registerButton.setOnClickListener{
            if(checkTextContext()){
                nickname = binding.nicknameRegisterPlainText.text.toString()
                password = binding.keyOnePassword.text.toString()
                registerPayload.put("nickname", nickname)
                registerPayload.put("password", password)
                if(client.isConnected) {
                    setOnRegisterSubscribe()
                    client.send("/mobEats/signUp", registerPayload.toString()).subscribe()
                } else {
                    context?.let { DynamicToast.makeError(it, getString(R.string.serverConnectionError)).show() }
                    connectToServer()
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun setOnRegisterSubscribe() {
        client.topic("/user/queue/register").subscribe { topicMessage ->
            registerReplyTmp = topicMessage.payload
            registerReply = JSONObject(registerReplyTmp)
            when {
                registerReply.getString("value") == "accept" -> {
                    activity?.runOnUiThread { context?.let { DynamicToast.makeSuccess(it, getString(R.string.successfulRegister)).show() } }
                    view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
                }
                registerReply.getString("value") == "reject" -> {
                    activity?.runOnUiThread { context?.let { DynamicToast.makeError(it, getString(R.string.failToRegister)).show() } }
                }
                else -> {
                    activity?.runOnUiThread { context?.let { DynamicToast.makeError(it, getString(R.string.noneError)).show() } }
                }
            }
        }
    }
}