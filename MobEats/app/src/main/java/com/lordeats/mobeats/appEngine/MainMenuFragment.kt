package com.lordeats.mobeats.appEngine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentMainMenuBinding


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

}