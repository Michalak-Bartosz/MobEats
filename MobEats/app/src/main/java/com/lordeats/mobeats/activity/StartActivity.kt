package com.lordeats.mobeats.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.lordeats.mobeats.LocaleHelper
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.ActivityStartBinding


class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start)

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

    private fun restartActivity() {

    }
}