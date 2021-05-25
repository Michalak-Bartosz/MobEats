package com.lordeats.mobeats.appEngine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentRestaurantListBinding
import com.lordeats.mobeats.events.MessageEvent
import com.lordeats.mobeats.events.MessageReplyEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class RestaurantListFragment : Fragment() {

    private lateinit var binding: FragmentRestaurantListBinding

    private lateinit var adapter: ArrayAdapter<*>
    private lateinit var restaurantList: ArrayList<String>

    private lateinit var messageToSend: MessageEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_list, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        val getRestaurantsList = JSONObject()
        getRestaurantsList.put("value", "getRestaurantsList")
        messageToSend = MessageEvent(getRestaurantsList)
        EventBus.getDefault().post(messageToSend)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun addRestaurantsToList(){

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, restaurantList)
        binding.restaurantListView.adapter = adapter
    }

    @Subscribe(sticky = false)
    fun onMessageReplyEvent(event: MessageReplyEvent){
        if(event.message?.getString("value") == "acceptListRestaurants"){

        }
    }
}