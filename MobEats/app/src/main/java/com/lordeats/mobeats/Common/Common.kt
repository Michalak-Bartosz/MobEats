package com.lordeats.mobeats.Common

import com.example.applicationfinal.Remote.IGoogleAPIService
import com.example.applicationfinal.Remote.RetrofitClient
import com.lordeats.mobeats.Model.Results


object Common {

    private val GOOGLE_API_URL="https://maps.googleapis.com/"

    var currentResult: Results?=null

    val googleApiService: IGoogleAPIService
        get()= RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}