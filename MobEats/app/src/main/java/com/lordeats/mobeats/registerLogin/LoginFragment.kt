package com.lordeats.mobeats.registerLogin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.lordeats.mobeats.R
import com.lordeats.mobeats.activity.AppActivity
import com.lordeats.mobeats.databinding.FragmentLoginBinding
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.net.SocketTimeoutException


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var intent: Intent

    private lateinit var client: StompClient
    private lateinit var nickname: String
    private lateinit var password: String
    private var loginReplyTmp: String = ""
    private lateinit var loginReply: JSONObject;
    private var loginPayload: JSONObject = JSONObject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        singUpButtonListener()
        singInButtonListener()
        connectToServer()
        clientLifecycleConfig()
        return binding.root
    }

    private fun connectToServer() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8080/app")
        client.connect()
    }

    @SuppressLint("CheckResult")
    private fun clientLifecycleConfig(){
        client.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> Log.d(TAG, "Stomp connection opened")
                LifecycleEvent.Type.ERROR -> {
                    Log.e(TAG, "Error", lifecycleEvent.exception)
                    activity?.runOnUiThread { context?.let { DynamicToast.makeError(it, getString(R.string.serverConnectionError)).show() } }
                    connectToServer()
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d(TAG, "Stomp connection closed")
                    connectToServer()
                }
                else -> Log.d(ContentValues.TAG, "Stomp connection none Error")
            }
        }
    }

    private fun checkTextContext(): Boolean {
        val loginCheck: String = binding.nicknameLoginPlainText.text.toString()
        val passwordCheck: String = binding.keyPassword.text.toString()

        return if(loginCheck.trim().isNotEmpty() && passwordCheck.trim().isNotEmpty()) {
            true
        }else{
            context?.let { DynamicToast.makeError(it, getString(R.string.noDataToSignIn)).show() }
            false
        }
    }

    private fun goToMainAppActivity(){
        intent = Intent(activity, AppActivity::class.java)
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, binding.nicknameLoginPlainText.text.toString())
        intent.type = "text/plain"
        startActivity(intent)
        activity?.finish()
    }

    private fun singInButtonListener() {
        binding.signInButton.setOnClickListener{
            if(checkTextContext()){
                nickname = binding.nicknameLoginPlainText.text.toString()
                password = binding.keyPassword.text.toString()
                loginPayload.put("nickname", nickname)
                loginPayload.put("password", password)
                if(client.isConnected) {
                    setLoginSubscribe()
                    client.send("/mobEats/signIn", loginPayload.toString()).subscribe()
                } else{
                    context?.let { DynamicToast.makeError(it, getString(R.string.serverConnectionError)).show() }
                    connectToServer()
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
                    activity?.runOnUiThread { context?.let { DynamicToast.makeError(it, getString(R.string.nicknameAndPasswordNotCorrect)).show() } }
                }
                else -> {
                    activity?.runOnUiThread { context?.let { DynamicToast.makeError(it, getString(R.string.noneError)).show() } }
                }
            }
        }
    }

    private fun singUpButtonListener(){
        binding.singUpButton.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}