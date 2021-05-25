package com.lordeats.mobeats.Common

import com.example.applicationfinal.Remote.IGoogleAPIService
import com.example.applicationfinal.Remote.RetrofitClient


object Common {

    private val GOOGLE_API_URL="https://maps.googleapis.com/"

    val googleApiService: IGoogleAPIService
        get()= RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}