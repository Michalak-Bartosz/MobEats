package com.lordeats.mobeats.registerLogin

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentLoginBinding
import com.lordeats.mobeats.events.MessageEvent
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var intent: Intent

    private lateinit var nickname: String
    private lateinit var password: String
    private var loginPayload: JSONObject = JSONObject()
    private lateinit var messageToSend: MessageEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        singUpButtonListener()
        singInButtonListener()
        return binding.root
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

    private fun singInButtonListener() {
        binding.signInButton.setOnClickListener{
            if(checkTextContext()){
                nickname = binding.nicknameLoginPlainText.text.toString()
                password = binding.keyPassword.text.toString()
                loginPayload.put("type","login")
                loginPayload.put("nickname", nickname)
                loginPayload.put("password", password)
                messageToSend = MessageEvent(loginPayload)
                EventBus.getDefault().post(messageToSend)
            }
        }
    }

    private fun singUpButtonListener(){
        binding.singUpButton.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.signInTextView.text = getString(R.string.signIn)
        binding.nicknameLoginPlainText.hint = getString(R.string.hintNickname)
        binding.keyPassword.hint = getString(R.string.hintPassword)
        binding.signInButton.text = getString(R.string.loginButton)
        binding.notSingUpComTextView.text = getString(R.string.notSingUpCom)
        binding.singUpButton.text = getString(R.string.singUpButton)
    }
}