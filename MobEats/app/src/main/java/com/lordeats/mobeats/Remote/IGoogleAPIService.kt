package com.example.applicationfinal.Remote

import com.lordeats.mobeats.Model.MyPlaces
import com.lordeats.mobeats.Model.PlaceDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import sk.antons.json.JsonObject

interface IGoogleAPIService {
    @GET
    fun getNearbyPlaces(@Url url:String): Call<MyPlaces>

    @GET
    fun getDetailPlace(@Url url:String): Call<PlaceDetail>

    @GET
    fun getDirections(@Url url:String): Call<String>

//    @GET("maps/api/directions/json")
//    fun getDirections(@Query("origin") origin:String,@Query("destination") destination: String):Call<String>
}
