package com.lordeats.mobeats.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.lordeats.mobeats.LocaleHelper
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app)
        changeLngButtonListenerConfig()
    }

    private fun changeLngButtonListenerConfig() {
        binding.changeLngButton.setOnClickListener {
            when {
                LocaleHelper.getLanguage(this) == "en" -> {
                    LocaleHelper.setLocale(this, "pl")
                    binding.changeLngButton.text = getString(R.string.additionalLng)
                }
                LocaleHelper.getLanguage(this) == "pl" -> {
                    LocaleHelper.setLocale(this, "en")
                    binding.changeLngButton.text = getString(R.string.additionalLng)
                }
                else -> {
                    LocaleHelper.setLocale(this, "pl")
                    binding.changeLngButton.text = getString(R.string.additionalLng)
                }
            }
        }
    }
}