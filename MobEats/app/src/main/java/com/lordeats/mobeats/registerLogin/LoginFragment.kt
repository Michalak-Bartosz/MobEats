package com.lordeats.mobeats.registerLogin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.lordeats.mobeats.R
import com.lordeats.mobeats.activity.AppActivity
import com.lordeats.mobeats.databinding.FragmentLoginBinding
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var intent: Intent

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

        if(loginCheck.trim().isNotEmpty() && passwordCheck.trim().isNotEmpty()) {
            context?.let { DynamicToast.makeSuccess(it, getString(R.string.successfulLogin)).show() }
            return true
        }else{
            context?.let { DynamicToast.makeError(it, getString(R.string.noDataToSignIn)).show() }
            return false
        }
    }

    private fun singInButtonListener() {
        binding.signInButton.setOnClickListener{
            if(checkTextContext()){
                intent = Intent(activity, AppActivity::class.java)
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, binding.nicknameLoginPlainText.text.toString())
                intent.type = "text/plain"
                startActivity(intent)
                activity?.finish()
            }
        }
    }

    private fun singUpButtonListener(){
        binding.singUpButton.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
}