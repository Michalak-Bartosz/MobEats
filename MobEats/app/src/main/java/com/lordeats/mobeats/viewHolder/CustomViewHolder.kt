package com.lordeats.mobeats.viewHolder

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.lordeats.mobeats.R
import com.lordeats.mobeats.listeners.CustomListeners
import com.lordeats.mobeats.viewModel.CustomViewModel
import com.lordeats.mobeats.viewModel.SwipeState

class CustomViewHolder : BaseViewHolder {

    companion object {
        private val TAG : String = CustomViewHolder::class.java.getSimpleName()
    }
    /**Data */
    private val imageView : ImageView
    private val textView : TextView
    /**With Events and Others */
    private val leftImage : ImageView
    private val rightImage : ImageView
    private val cardView : CardView

    constructor(context : Context, itemView : View,customListeners : CustomListeners) : super(context, itemView, customListeners) {
        imageView = itemView.findViewById(R.id.image_view)
        textView = itemView.findViewById(R.id.restaurantNameTextView)
        cardView = itemView.findViewById(R.id.card_view)
        leftImage = itemView.findViewById(R.id.infoButton)
        rightImage = itemView.findViewById(R.id.deleteButton)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bindDataToViewHolder(item : CustomViewModel, position : Int, swipeState : SwipeState) {
        //region Input Data
        imageView.setBackgroundResource(item.icon?:0)
        textView.text = item.name
        setSwipe(cardView, item.state)
        //endregion
        //region Set Event Listener
        /* On Click */
        leftImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view : View?) {
                getListener().onClickLeft(item, position)
            }
        })
        rightImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view : View?) {
                getListener().onClickRight(item, position)
            }
        })
        cardView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view : View?) { //Do not remove this need this click listener to swipe with on touch listener
                LogDebug(TAG, "on Click Card")
            }
        })
        /* On Touch Swipe */
        cardView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view : View, event : MotionEvent) : Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dXLead = view.x - event.rawX
                        dXTrail = view.right - event.rawX
                        LogDebug(TAG, "MotionEvent.ACTION_DOWN")
                        false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.parent.requestDisallowInterceptTouchEvent(true)
                        onAnimate(view, onSwipeMove(event.rawX + dXLead, event.rawX + dXTrail,swipeState),0)
                        item.state = getSwipeState(event.rawX + dXLead, event.rawX + dXTrail, swipeState)
                        LogDebug(TAG, "MotionEvent.ACTION_MOVE")
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        onAnimate(view, onSwipeUp(item.state),250)
                        LogDebug(TAG, "MotionEvent.ACTION_UP")
                        false
                    }
                    else -> true
                }
            }
        })
        //endregion
    }
}