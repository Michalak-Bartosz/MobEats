package com.lordeats.mobeats.activity

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ActivityNavigator
import com.example.applicationfinal.Remote.IGoogleAPIService
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.lordeats.mobeats.Common.Common
import com.lordeats.mobeats.Helper.DirectionJsonParser
import com.lordeats.mobeats.Model.MyPlaces
import com.lordeats.mobeats.Model.PlaceDetail
import com.lordeats.mobeats.R
import com.lordeats.mobeats.Remote.InfoWindowModification
import com.lordeats.mobeats.databinding.ActivityMapsBinding
import com.lordeats.mobeats.databinding.InfoWindowBinding
import com.lordeats.mobeats.events.MessageEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val TAG = "MyActivity"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var mLastLocation: Location
    private var mMarker: Marker? = null
    private var markerList: ArrayList<Marker> = ArrayList()

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    lateinit var filterList: Array<String>
    lateinit var spinnerAdapter: ArrayAdapter<*>

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var mService: IGoogleAPIService
    lateinit var mDetails: IGoogleAPIService
    internal lateinit var currentPlace: MyPlaces
    var mPlace: PlaceDetail? = null

    private var userDataTmp: String = ""
    private var userData: JSONObject = JSONObject()

    var name: String? = null
    var rating: String? = null
    var address: String? = null
    var phoneNumber: String? = null
    var website: String? = null
    var priceLevel: String? = null
    var typePlace: String = "restaurant"
    var infoWindowPayload: JSONObject = JSONObject()
    var findPplPayload: JSONObject = JSONObject()
    private lateinit var messageToSend: MessageEvent

    var polyLine: Polyline ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Inicjalizacja serwisu
        mService = Common.googleApiService
        mDetails = Common.googleApiService

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest()
                buildLocationCallback()
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
        } else {
            buildLocationRequest()
            buildLocationCallback()
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

        onSelectedItemSpinnerListener()
        changeLngButtonListenerConfig()
        changeModeButtonListenerConfig()
        findFoodButtonListenerConfig()

        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun getUserData() {
        userDataTmp = intent.getStringExtra("USER_DATA_FIND_PPL").toString()
        if(userDataTmp != "null") {
            userData = JSONObject(userDataTmp)
            setPplMarker()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getUserData()
        Places.initialize(applicationContext, R.string.google_maps_key.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                map.isMyLocationEnabled = true
            }
        } else {
            map.isMyLocationEnabled = true
        }
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener { marker ->
            if(isMarkerFromUser(marker)){
                val originLatLng = LatLng(marker.position.latitude,marker.position.longitude)
                val destinationLatLng = LatLng(mLastLocation.latitude,mLastLocation.longitude)
                drawRoutes(originLatLng,destinationLatLng)
            }
            if (marker != mMarker && isMarkerFromUser(marker) == false) {
                Common.currentResult = currentPlace!!.results!![Integer.parseInt(marker.snippet)]
                mDetails.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
                    .enqueue(object : retrofit2.Callback<PlaceDetail> {
                        override fun onResponse(
                            call: Call<PlaceDetail>,
                            response: Response<PlaceDetail>
                        ) {
                            mPlace = response!!.body()
                            if (mPlace!!.result!!.name == null) "-" else name =
                                mPlace!!.result!!.name
                            if (mPlace!!.result!!.rating.toString() == null) "-" else rating =
                                mPlace!!.result!!.rating.toString()
                            if (mPlace!!.result!!.formatted_address == null) "-" else address =
                                mPlace!!.result!!.formatted_address
                            if (mPlace!!.result!!.formatted_phone_number == null) phoneNumber =
                                "-" else phoneNumber = mPlace!!.result!!.formatted_phone_number
                            if (mPlace!!.result!!.website == null) website = "-" else website =
                                mPlace!!.result!!.website
                            if (mPlace!!.result!!.price_level.toString() == null) "-" else priceLevel =
                                mPlace!!.result!!.price_level.toString()
                            Log.d("URL_PHONE", "" + name)
                            Log.d("URL_PHONE", "" + rating)
                            Log.d("URL_PHONE", "" + address)
                            Log.d("URL_PHONE", "" + phoneNumber)
                            Log.d("URL_PHONE", "" + website)
                            Log.d("URL_PHONE", "" + priceLevel)

                            infoWindowPayload = JSONObject()
                            infoWindowPayload.put("type", "addRestaurant")
                            infoWindowPayload.put("name", name)
                            infoWindowPayload.put("ratingPoints", rating)
                            infoWindowPayload.put("address", address)
                            infoWindowPayload.put("fonNumber", phoneNumber)
                            infoWindowPayload.put("webPage", website)
                            infoWindowPayload.put("priceLevel", priceLevel)
                            setInfoWindowListenerConfig()
                            infoWindowPayload.remove("priceLevel")
                            val priceLevelArray = resources.getStringArray(R.array.priceLevelsArray)
                            infoWindowPayload.put(
                                "priceLevel",
                                priceLevelArray[priceLevel?.toInt()!!]
                            )

                            map.setInfoWindowAdapter(
                                InfoWindowModification(
                                    context = this@MapsActivity,
                                    content = infoWindowPayload.toString()
                                )
                            )
                            if (marker.isInfoWindowShown) {
                                marker.hideInfoWindow()
                            } else {
                                marker.showInfoWindow()
                            }
                        }

                        override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                            Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            true
        }

    }

    private fun isMarkerFromUser(marker: Marker): Boolean {
        for (mar in markerList) {
            if (mar == marker) {
                return true
            }
        }
        return false
    }

    private fun setInfoWindowListenerConfig() {
        map.setOnInfoWindowClickListener {
            messageToSend = MessageEvent(infoWindowPayload)
            EventBus.getDefault().post(messageToSend)
        }
    }

    private fun getPlaceDetailUrl(place_id: String): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?placeid=$place_id")
        url.append("&key=AIzaSyCoZwNDKs4JRA3HNZCKmB_c09GH0bLPnEE")
        Log.d("URL_DETAIL", "" + url.toString())
        return url.toString()
    }

    private fun nearByPlace(nextPageToken: String) {
        map.clear()
        var pageToken = ""
        val url = getUrl(latitude, longitude, typePlace, nextPageToken)
        mService.getNearbyPlaces(url)
            .enqueue(object : Callback<MyPlaces> {
                override fun onResponse(call: Call<MyPlaces>?, response: Response<MyPlaces>?) {
                    currentPlace = response!!.body()!!
                    if (response!!.isSuccessful) {
                        for (i in 0 until response.body()!!.results!!.size) {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry!!.location!!.lat
                            val lng = googlePlace.geometry!!.location!!.lng
                            val placeName = googlePlace.name
                            val latLng = LatLng(lat, lng)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)
                            markerOptions.snippet(i.toString())
                            map!!.addMarker(markerOptions)
                        }
                    }
                }

                override fun onFailure(call: Call<MyPlaces>?, t: Throwable) {
                    Toast.makeText(baseContext, "" + t!!.message, Toast.LENGTH_SHORT).show()
                    Log.d("URL_DEBUG", "" + t!!.message);
                }
            })
    }

    private fun getUrl(
        latitude: Double,
        longitude: Double,
        typePlace: String,
        nextPage: String
    ): String {
        val googlePlaceUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        if (nextPage.length > 0) {
            googlePlaceUrl.append("?key=AIzaSyCoZwNDKs4JRA3HNZCKmB_c09GH0bLPnEE")
            googlePlaceUrl.append("&pagetoken=$nextPage")
        } else {
            googlePlaceUrl.append("?location=$latitude,$longitude")
            googlePlaceUrl.append("&radius=1000")
            googlePlaceUrl.append("&type=$typePlace")
            googlePlaceUrl.append("&key=AIzaSyCoZwNDKs4JRA3HNZCKmB_c09GH0bLPnEE")
        }
        Log.d("URL_DEBUG", "Request: " + googlePlaceUrl.toString());

        return googlePlaceUrl.toString()
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0!!.locations.size - 1)

                if (mMarker != null) {

                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("You are here ;-)")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                mMarker = map!!.addMarker(markerOptions)
                val cameraPosition = CameraPosition.Builder()
                    .target(latLng).zoom(16f).build()
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                findPplButtonListenerConfig()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            else
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSION_CODE
                )
            return false
        } else
            return true
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildDirectionRequest(origin: LatLng, destiny: LatLng): String {
        var lat = origin.latitude
        var long = origin.longitude
        val googleDirectionUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/directions/json?")
        googleDirectionUrl.append("origin=$lat,$long")
        lat = destiny.latitude
        long = destiny.longitude
        googleDirectionUrl.append("&destination=$lat,$long")
//        googleDirectionUrl.append("&mode=driving")
        googleDirectionUrl.append("&key=AIzaSyCoZwNDKs4JRA3HNZCKmB_c09GH0bLPnEE")
        return googleDirectionUrl.toString()
    }

    private fun drawRoutes(origin: LatLng, destiny: LatLng) {
        if (polyLine != null) {
            polyLine!!.remove()
        }
        val googleDirectionUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/directions/json?").append("origin=")
                .append(origin.latitude.toString())
                .append(",")
                .append(origin.longitude.toString())
                .append("&destination=")
                .append(destiny.latitude.toString())
                .append(",")
                .append(destiny.longitude.toString())
                .append("&key=AIzaSyCoZwNDKs4JRA3HNZCKmB_c09GH0bLPnEE")
                .toString()
        Log.d("LINK",googleDirectionUrl)
        mService.getDirections(googleDirectionUrl)
            .enqueue(object:Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val parsedJson = parserTask(response!!.body()!!.toString())
                    createRoute(parsedJson)
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("DIRECT","Direction API Failure - Thank's google :-)")
                }

            })
    }

    private fun parserTask(vararg response: String): List<List<HashMap<String,String>>>? {
        val jsonObject: JSONObject
        var routes: List<List<HashMap<String,String>>>?=null
        try {
            jsonObject = JSONObject(response[0])
            val parser = DirectionJsonParser()
            routes = parser.parse(jsonObject)
        }catch (e:JSONException) {
            e.printStackTrace()
        }
        return routes
    }

    private fun createRoute(result: List<List<HashMap<String,String>>>?) {
        var points:ArrayList<LatLng>?=null
        var polylineOptions:PolylineOptions?=null

        for(i in result!!.indices){
            points = ArrayList()
            polylineOptions = PolylineOptions()

            val path:List<HashMap<String,String>> = result[i]

            for(j in path.indices) {
                val point = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position = LatLng(lat,lng)

                points.add(position)
            }
            polylineOptions.addAll(points)
            polylineOptions.width(12f)
            polylineOptions.color(Color.RED)
            polylineOptions.geodesic(true)
        }
        polyLine = map!!.addPolyline(polylineOptions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()
                            fusedLocationProviderClient =
                                LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                            map.isMyLocationEnabled = true
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onSelectedItemSpinnerListener() {
        filterList = resources.getStringArray(R.array.MapFilters)
        spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, filterList)
        binding.mapFilterSpinner.adapter = spinnerAdapter
        binding.mapFilterSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?, position: Int, id: Long
            ) {
                if (position == 0) {
                    typePlace = "restaurant"
                } else if (position == 1) {
                    typePlace = "cafe"
                } else {
                    typePlace = "bar"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    ////// Tutaj dokonczyc
    private fun findFoodButtonListenerConfig() {
        binding.findFoodOnMapButton.setOnClickListener {
            nearByPlace("")
        }
    }

    private fun findPplButtonListenerConfig() {
        binding.findPplButton.setOnClickListener {
            findPplPayload = JSONObject()
            findPplPayload.put("type", "findPpl")
            findPplPayload.put("lat", mLastLocation.latitude)
            findPplPayload.put("long", mLastLocation.longitude)
            messageToSend = MessageEvent(findPplPayload)
            EventBus.getDefault().post(messageToSend)
        }
    }

    private fun changeLngButtonListenerConfig() {
        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()

        binding.changeLngMapButton.setOnClickListener {
            when (binding.changeLngMapButton.text) {
                "PL" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "pl")
                    updateResources("pl")
                    binding.changeLngMapButton.text = getString(R.string.additionalLng)
                }
                "ENG" -> {
                    sharedPrefsEdit.putString("Locale.Helper.Selected.Language", "en")
                    updateResources("en")
                    binding.changeLngMapButton.text = getString(R.string.additionalLng)
                }
                else -> {
                }
            }
            sharedPrefsEdit.apply()
        }
    }

    @Suppress("DEPRECATION")
    private fun updateResources(language: String) {
        val myLocale = Locale(language)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        onConfigurationChanged(conf)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.findFoodOnMapButton.text = getString(R.string.findFoodOnMap)
        binding.typeOfPremisesTextView.text = getString(R.string.typeOfPremises)
        filterList = resources.getStringArray(R.array.MapFilters)
        spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, filterList)
        binding.mapFilterSpinner.adapter = spinnerAdapter
    }

    private fun changeModeButtonListenerConfig() {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.changeModeMapButton.setImageResource(R.drawable.ic_light_mode)
            binding.findPplButton.setImageResource(R.drawable.ic_find_ppl_dark)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.changeModeMapButton.setImageResource(R.drawable.ic_dark_mode)
            binding.findPplButton.setImageResource(R.drawable.ic_find_ppl_white)
        }

        binding.changeModeMapButton.setOnClickListener {

            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                binding.changeModeMapButton.setImageResource(R.drawable.ic_dark_mode)
                binding.findPplButton.setImageResource(R.drawable.ic_find_ppl_white)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                binding.changeModeMapButton.setImageResource(R.drawable.ic_light_mode)
                binding.findPplButton.setImageResource(R.drawable.ic_find_ppl_dark)
            }
            sharedPrefsEdit.apply()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.message!!.getString("type") == "findPplReply") {
            userDataTmp = event.message!!.toString()
            userData = JSONObject(userDataTmp)
            if(userDataTmp != "null")
                setPplMarker()
        }
    }

    private fun setPplMarker() {
        val lat: Double = userData.getString("lat").toDouble()
        val long: Double = userData.getString("long").toDouble()
        val userMarkerOption = MarkerOptions()
            .position(LatLng(lat,long))
            .title("Eat with me!")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        val marker = map!!.addMarker(userMarkerOption)
        if(marker != null)
            markerList.add(marker)
    }
    
}