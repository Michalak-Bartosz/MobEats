package com.lordeats.mobeats.registerLogin

import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentRegisterBinding
import com.lordeats.mobeats.events.MessageEvent
import com.lordeats.mobeats.events.MessageReplyEvent
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var nickname: String
    private lateinit var password: String
    private var registerPayload: JSONObject = JSONObject()
    private lateinit var messageToSend: MessageEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerButtonListener()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(MessageReplyEvent::class.java)){
            EventBus.getDefault().unregister(this)
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
                registerPayload.put("type", "register")
                registerPayload.put("nickname", nickname)
                registerPayload.put("password", password)
                messageToSend = MessageEvent(registerPayload)
                EventBus.getDefault().post(messageToSend)
            }
        }
    }

    @Subscribe(sticky = false)
    fun onMessageReplyEvent(event: MessageReplyEvent){
        if(event.message == "acceptRegister"){
            EventBus.getDefault().unregister(this)
            this.view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}