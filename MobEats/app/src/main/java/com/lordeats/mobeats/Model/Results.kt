package com.lordeats.mobeats.Model

import com.google.android.libraries.places.api.model.OpeningHours
import com.lordeats.mobeats.Model.Geometry

class Results {
    var name:String?=null
    var icon:String?=null
    var geometry:Geometry?=null
    var photos:Array<Photos>?=null
    var id:String?=null
    var place_id:String?=null
    var price_level:Int=0
    var rating:Double=0.0
    var reference:String?=null
    var scope:String?=null
    var types:Array<String>?=null
    var vicinity:String?=null
    var address_components:Array<AddressComponent>?=null
    var adr_address:String?=null
    var formatted_address:String?=null
    var formatted_phone_number:String?=null
    var international_phone_number:String?=null
    var url:String?=null
    var reviews:Array<Review>?=null
    var utc_offest:Int=0
    var website:String?=null
    var business_status:String?=null
//    var opening_hours:OpeningHours?=null
}