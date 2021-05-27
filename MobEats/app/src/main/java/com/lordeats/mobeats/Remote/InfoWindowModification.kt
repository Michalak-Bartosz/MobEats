package com.lordeats.mobeats.Remote

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.lordeats.mobeats.Common.Common
import com.lordeats.mobeats.Model.PlaceDetail
import com.lordeats.mobeats.R
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response

class InfoWindowModification(context: Context, content: String) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.info_window, null)
    var context: JSONObject = JSONObject(content)

    private fun renDowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.title)
        tvTitle.text = marker.title

        val rating = (view.findViewById(R.id.ratingText) as TextView)
        rating.text = context.getString("ratingPoints")
        val address = (view.findViewById(R.id.addressText) as TextView)
        address.text = context.getString("address")
        val fonNumber = (view.findViewById(R.id.fonNumberText) as TextView)
        fonNumber.text = context.getString("fonNumber")
        val priceLevel = (view.findViewById(R.id.priceLevelText) as TextView)
        priceLevel.text = context.getString("priceLevel")
        val webPage = (view.findViewById(R.id.webPageText) as TextView)
        webPage.text = context.getString("webPage")
    }

    override fun getInfoContents(marker: Marker): View {
        renDowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        renDowWindowText(marker, mWindow)
        return mWindow
    }
}