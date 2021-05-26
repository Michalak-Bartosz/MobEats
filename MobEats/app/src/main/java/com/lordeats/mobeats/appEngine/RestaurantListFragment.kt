package com.lordeats.mobeats.appEngine

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.gson.JsonParser
import com.lordeats.mobeats.R
import com.lordeats.mobeats.databinding.FragmentRestaurantListBinding
import com.lordeats.mobeats.events.ChangeLangEvent
import com.lordeats.mobeats.events.MessageEvent
import com.lordeats.mobeats.events.MessageReplyEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class RestaurantListFragment : Fragment() {

    private lateinit var binding: FragmentRestaurantListBinding

    private var restaurantList: ArrayList<String> = ArrayList()

    private lateinit var messageToSend: MessageEvent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_list, container, false)
        if(!EventBus.getDefault().hasSubscriberForEvent(MessageReplyEvent::class.java)){
            EventBus.getDefault().register(this)
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().hasSubscriberForEvent(MessageReplyEvent::class.java)){
            EventBus.getDefault().register(this)
        }
        val getRestaurantsList = JSONObject()
        getRestaurantsList.put("type", "getRestaurantsList")
        messageToSend = MessageEvent(getRestaurantsList)
        EventBus.getDefault().post(messageToSend)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }




    @Suppress("DEPRECATION")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageReplyEvent(event: MessageReplyEvent){
        if(event.message != null){
            val adapter: ArrayAdapter<*> = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, restaurantList)
            if(restaurantList.isNotEmpty())
                adapter.clear()
            Log.d("Bartek", event.message.toString())
            val jsonParser = JsonParser()
            val jsonElement = jsonParser.parse(event.message.toString())
            val replayDataList = jsonElement.asJsonArray
            for(value in replayDataList) {
                val valueJ = JSONObject(value.toString())
                restaurantList.add(valueJ.toString())
            }
            binding.restaurantListView.adapter = adapter
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeLangEvent(event: ChangeLangEvent){
        if(event.message == "newLang"){
            //TODO dopisać aktualizację tekstu jak się pojawi
        }
    }
}