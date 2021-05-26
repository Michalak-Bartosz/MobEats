package com.lordeats.mobeats.appEngine

import android.content.res.Configuration
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
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class ManageAccountMenuFragment : Fragment() {

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
        deleteAccountButtonListener()
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

    private fun deleteAccountButtonListener(){
        binding.dellAccountButton.setOnClickListener {
            userDataPayload.put("type", "deleteAccount")
            messageToSend = MessageEvent(userDataPayload)
            EventBus.getDefault().post(messageToSend)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.dellAccountButton.text = getString(R.string.deleteAccountButton)
        binding.newNicknameText.text = getString(R.string.newNicknameText)
        binding.changeNicknameText.text = getString(R.string.changeNicknameText)
        binding.editNicknameMaPlainText.hint = getString(R.string.changeNicknameHint)
        binding.changeNicknameButton.text = getString(R.string.changeButton)

        binding.changePasswordText.text = getString(R.string.changePasswordText)
        binding.newPasswordText.text = getString(R.string.newPasswordText)
        binding.oldPasswordText.text = getString(R.string.oldPasswordText)
        binding.keyNewMaPassword.hint = getString(R.string.changePasswordNewHint)
        binding.keyOldMaPassword.hint = getString(R.string.changePasswordOldHint)
        binding.changePasswordButton.text = getString(R.string.changeButton)
    }
}