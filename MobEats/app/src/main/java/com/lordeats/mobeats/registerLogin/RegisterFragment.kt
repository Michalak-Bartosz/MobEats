package com.lordeats.mobeats.registerLogin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentRegisterBinding
import com.pranavpandey.android.dynamic.toasts.DynamicToast

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerButtonListener()
        return binding.root
    }

    private fun checkTextContext(): Boolean {
        val loginCheck: String = binding.nicknameRegisterPlainText.text.toString()
        val passwordOneCheck: String = binding.keyOnePassword.text.toString()
        val passwordTwoCheck: String = binding.keyTwoPassword.text.toString()

        if(loginCheck.trim().isNotEmpty() && passwordOneCheck.trim().isNotEmpty() && passwordTwoCheck.trim().isNotEmpty()) {
            if(passwordOneCheck == passwordTwoCheck){
                TODO("Dodać powiązanie z funkcją register w StartActivity")
                return true
            }else{
                context?.let { DynamicToast.makeError(it, getString(R.string.keyPasswordNotEqual)).show() }
                return false
            }
        }else{
            context?.let { DynamicToast.makeError(it, getString(R.string.noDataToSingUp)).show() }
            return false
        }
    }

    private fun registerButtonListener() {
        binding.registerButton.setOnClickListener{
            if(checkTextContext()){
                view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }
}