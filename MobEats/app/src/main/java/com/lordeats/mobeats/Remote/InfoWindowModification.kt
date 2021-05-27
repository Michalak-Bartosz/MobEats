package com.lordeats.mobeats.Remote

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.lordeats.mobeats.R

class InfoWindowModification(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){
        //        val tvSnippet = view.findViewById<TextView>(R.id.snippet)
//        tvSnippet.text = marker.snippet

        val tvTitle = view.findViewById<TextView>(R.id.title)
        tvTitle.text = marker.title

        val ratingPoint = view.findViewById<TextView>(R.id.ratingText)

//        ratingPointText: String, addressText: String, priceLevelText: String, fonNumberText: String, webPageText: String
    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}