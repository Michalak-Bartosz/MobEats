package com.lordeats.mobeats.appEngine

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.lordeats.mobeats.R
import com.lordeats.mobeats.activity.AppActivity
import com.lordeats.mobeats.activity.MapsActivity
import com.lordeats.mobeats.databinding.FragmentMainMenuBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main_menu, container, false)
        findFoodButtonListener()
        favouriteRestaurantButtonListener()
        manageAccountButtonListener()
        return binding.root
    }

    private fun manageAccountButtonListener() {
        binding.manageAccountButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_manageAccountFragment)
        }
    }

    private fun findFoodButtonListener() {
        binding.findFoodButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_MapsActivity)
        }
    }

    private fun favouriteRestaurantButtonListener() {
        binding.favouriteRestaurantButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainMenuFragment_to_restaurantListFragment)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.findFoodButton.text = getString(R.string.findFoodButton)
        binding.favouriteRestaurantButton.text = getString(R.string.favouriteRestaurantsButton)
        binding.manageAccountButton.text =getString(R.string.manageAccountButton)
    }
}