package com.lordeats.mobeats.appEngine.manageAccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentManageAccountBinding
import com.lordeats.mobeats.events.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject


class ManageAccountMenu : Fragment() {

    private lateinit var binding: FragmentManageAccountBinding
    private lateinit var nickname: String
    private lateinit var password: String
    private var userDataPayload: JSONObject = JSONObject()
    private lateinit var messageToSend: MessageEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_account, container, false)
        changeButtonsListener()
        return binding.root
    }

    private fun changeButtonsListener(){

        binding.changeNicknameButton.setOnClickListener {
            nickname = binding.editNicknameMaPlainText.text.toString()
            userDataPayload.put("type", "changeData")
            userDataPayload.put("data", "nickname")
            userDataPayload.put("nickname", nickname)
            messageToSend = MessageEvent(userDataPayload)
            EventBus.getDefault().post(messageToSend)
        }

        binding.changePasswordButton.setOnClickListener {
            password = binding.keyNewMaPassword.text.toString()
            userDataPayload.put("type", "changeData")
            userDataPayload.put("data", "password")
            userDataPayload.put("password", password)
            messageToSend = MessageEvent(userDataPayload)
            EventBus.getDefault().post(messageToSend)
        }
    }

}