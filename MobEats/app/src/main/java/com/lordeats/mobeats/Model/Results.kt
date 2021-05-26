package com.lordeats.mobeats.Model

import com.google.android.libraries.places.api.model.OpeningHours
import com.lordeats.mobeats.Model.Geometry

class Results {
    var next_page_token:String?=null
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
//    var opening_hours:OpeningHours?=null
}