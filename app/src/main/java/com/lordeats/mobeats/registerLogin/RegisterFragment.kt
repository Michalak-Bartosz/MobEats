package com.lordeats.mobeats.registerLogin

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.lordeats.mobeats.R
import com.lordeats.mobeats.activity.AppActivity
import com.lordeats.mobeats.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
const val EXTRA_MESSAGE = "com.lordeats.mobeats.MESSAGE"

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var intent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        binding.registerButton.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_appActivity) }
        return binding.root
    }

    fun register(view: View){
        var nickname = binding.nicknamePlainText.text.toString()
        intent = Intent(this.activity, AppActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, nickname)
        }
        startActivity(intent)
    }
}