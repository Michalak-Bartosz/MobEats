package com.lordeats.mobeats.viewHolder

import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.lordeats.mobeats.listeners.CustomListeners
import com.lordeats.mobeats.viewModel.CustomViewModel
import com.lordeats.mobeats.viewModel.SwipeState

@Suppress("DEPRECATION")
abstract class BaseViewHolder : RecyclerView.ViewHolder {

    companion object {
        private val TAG : String = BaseViewHolder::class.java.simpleName

        fun LogDebug(TAG : String, message : String) {
            Log.d(TAG,message)
        }

        fun LogError(TAG : String, message : String) {
            Log.e(TAG,message)
        }
    }
    /** Main */
    private val context : Context
    private val customListeners : CustomListeners
    /** On Swipe */
    private val size : Point
    private val display : Display
    private val windowManager : WindowManager
    private val cardViewLeading : Float
    private val cardViewLeadEdge : Float
    private val cardViewTrailEdge : Float
    private val cardViewTrailing : Float
    var dXLead : Float = 0.toFloat()
    var dXTrail : Float = 0.toFloat()

    constructor(context: Context, itemView: View, customListeners: CustomListeners) : super(itemView) {
        this.context = context
        this.customListeners = customListeners
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        size = Point()
        display = windowManager.defaultDisplay //activity.getWindowManager().getDefaultDisplay()
        display.getSize(size)
        cardViewLeading = size.x.toFloat() * 0.10f //leading
        cardViewLeadEdge = size.x.toFloat() * 0.25f //leading_rubber
        cardViewTrailEdge = size.x.toFloat() * 0.75f //trailing_rubber
        cardViewTrailing = size.x.toFloat() * 0.90f //trailing
    }

    fun getListener() : CustomListeners {
        return customListeners
    }

    fun setSwipe(view : View, swipeState : SwipeState) {
        onAnimate(view, onSwipeUp(swipeState),0)
    }

    fun onAnimate(view : View, dx : Float, duration : Long) {
        view.animate().x(dx).setDuration(duration).start()
    }

    fun onSwipeMove(currentLead : Float, currentTrail : Float, swipeState : SwipeState) : Float {
        LogDebug(TAG,"onSwipeMove($currentLead, $currentTrail, $swipeState)")
        return when (swipeState) {
            SwipeState.LEFT, SwipeState.RIGHT, SwipeState.LEFT_RIGHT -> {
                currentLead
            }
            else -> {
                /**swipeState == SwipeState.NONE*/
                /**swipeState == SwipeState.NONE*/
                LogDebug(TAG,"Else Swipe $swipeState")
                cardViewLeading
            }
        }
    }

    fun getSwipeState(currentLead : Float, currentTrail : Float, swipeState : SwipeState) : SwipeState {
        LogDebug(TAG,"getSwipeState($currentLead, $currentTrail, $swipeState)")
        return when {
            swipeState == SwipeState.LEFT && currentLead < cardViewLeading && currentTrail < cardViewTrailEdge -> {
                LogDebug(TAG,"SwipeState.LEFT")
                SwipeState.LEFT
            }
            swipeState == SwipeState.RIGHT && currentLead > cardViewLeadEdge && currentTrail > cardViewTrailing -> {
                LogDebug(TAG,"SwipeState.RIGHT")
                SwipeState.RIGHT
            }
            swipeState == SwipeState.LEFT_RIGHT && currentLead < cardViewLeading && currentTrail < cardViewTrailEdge -> {
                LogDebug(TAG,"SwipeState.LEFT")
                SwipeState.LEFT
            }
            swipeState == SwipeState.LEFT_RIGHT && currentLead > cardViewLeadEdge && currentTrail > cardViewTrailing -> {
                LogDebug(TAG,"SwipeState.RIGHT")
                SwipeState.RIGHT
            }
            else -> {
                LogDebug(TAG,"SwipeState.NONE")
                SwipeState.NONE
            }
        }
    }

    fun onSwipeUp(swipeState : SwipeState) : Float {
        LogDebug(TAG,"onSwipeUp($swipeState)")
        LogDebug(TAG,"$cardViewLeading $cardViewLeadEdge $cardViewTrailEdge $cardViewTrailing - ${size.x.toFloat()}")
        return when (swipeState) {
            SwipeState.NONE -> cardViewLeading
            SwipeState.LEFT -> (size.x.toFloat() * -0.05f)
            SwipeState.RIGHT -> cardViewLeadEdge
            else -> cardViewLeading
        }
    }

    abstract fun bindDataToViewHolder(item : CustomViewModel, position : Int, swipeState : SwipeState)
}