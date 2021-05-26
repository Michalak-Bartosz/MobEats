package com.lordeats.mobeats.appEngine

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.lordeats.mobeats.R
import com.lordeats.mobeats.adapter.CustomAdapter
import com.lordeats.mobeats.databinding.FragmentRestaurantListBinding
import com.lordeats.mobeats.events.MessageEvent
import com.lordeats.mobeats.events.MessageReplyEvent
import com.lordeats.mobeats.listeners.CustomListeners
import com.lordeats.mobeats.manage.CustomLinearLayoutManager
import com.lordeats.mobeats.viewModel.CustomViewModel
import com.lordeats.mobeats.viewModel.SwipeState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

const val KEY_TEXT_LIST_RESTAURANT = "key_textListRestaurant"

class RestaurantListFragment : Fragment(), CustomListeners {

    private lateinit var adapter : CustomAdapter
    private lateinit var itemList : MutableList<CustomViewModel>
    private lateinit var informationItemList: JsonArray
    private var icon: Int = 0

    private lateinit var binding: FragmentRestaurantListBinding

    private lateinit var messageToSend: MessageEvent

    private lateinit var textListRestaurant: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant_list, container, false)
        if(!EventBus.getDefault().hasSubscriberForEvent(MessageReplyEvent::class.java)){
            EventBus.getDefault().register(this)
        }
        setRecyclerView()
        if(savedInstanceState != null){
            textListRestaurant = savedInstanceState.getString(KEY_TEXT_LIST_RESTAURANT)!!
            setItems()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_TEXT_LIST_RESTAURANT, informationItemList.toString())
    }

    private fun setRecyclerView() {
        adapter = CustomAdapter(this, SwipeState.LEFT_RIGHT)
        binding.recyclerView.layoutManager = CustomLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }

    @Suppress("DEPRECATION")
    private fun setItems() {
        itemList = mutableListOf()
        itemList.clear()
        val jsonParser = JsonParser()
        val jsonElement = jsonParser.parse(textListRestaurant)
        informationItemList = jsonElement.asJsonArray

        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {icon = R.drawable.ic_restaurant_light}
            Configuration.UI_MODE_NIGHT_NO -> {icon = R.drawable.ic_restaurant_dark}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {icon = R.drawable.ic_restaurant_dark}
        }

        for(value in informationItemList) {
            val valueJ = JSONObject(value.toString())
            itemList.add(CustomViewModel(icon, valueJ.getString("name")))
        }
        adapter.setItems(itemList)
    }

    override fun onClickLeft(item : CustomViewModel, position : Int) {
        val valueJ = JSONObject(informationItemList[position].toString())
        showDialog(valueJ.getString("name"), valueJ.getString("ratingPoints"), valueJ.getString("address"), valueJ.getString("fonNumber"), valueJ.getString("emailAddress"), valueJ.getString("webPage"))
    }

    override fun onClickRight(item : CustomViewModel, position : Int) {
        itemList.remove(item)
        informationItemList.remove(position)
        adapter.setItems(itemList)
    }



    @SuppressLint("CutPasteId")
    @Suppress("NAME_SHADOWING")
    private fun showDialog(titleText: String, ratingText: String, addressText: String, fonNumberText: String, emailText: String, webPageText: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        val title = (dialog.findViewById(R.id.dialogTitle) as TextView)
        title.text = titleText
        val rating = (dialog.findViewById(R.id.ratingText) as TextView)
        rating.text = ratingText
        val address = (dialog.findViewById(R.id.addressText) as TextView)
        address.text = addressText
        val fonNumber = (dialog.findViewById(R.id.fonNumberText) as TextView)
        fonNumber.text = fonNumberText
        val email = (dialog.findViewById(R.id.emailText) as TextView)
        email.text = emailText
        val webPage = (dialog.findViewById(R.id.webPageText) as TextView)
        webPage.text = webPageText

        val okBtn = dialog.findViewById(R.id.okButton) as Button
        okBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Suppress("DEPRECATION")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageReplyEvent(event: MessageReplyEvent){
        if(event.message != null){
            textListRestaurant = event.message.toString()
            setItems()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.restaurantsTextView.text = getString(R.string.restaurants)
    }
}