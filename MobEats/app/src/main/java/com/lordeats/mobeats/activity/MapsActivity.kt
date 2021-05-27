package com.lordeats.mobeats.activity

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
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
import com.lordeats.mobeats.Model.MyPlaces
import com.lordeats.mobeats.Model.PlaceDetail
import com.lordeats.mobeats.R
import com.lordeats.mobeats.Remote.InfoWindowModification
import com.lordeats.mobeats.databinding.ActivityMapsBinding
import com.lordeats.mobeats.databinding.InfoWindowBinding
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val TAG = "MyActivity"
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var mLastLocation: Location
    private var mMarker: Marker?= null

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
    var mPlace:PlaceDetail?=null
    var typePlace = "restaurant"

    var name:String?=null
    var rating:String?=null
    var address:String?=null
    var phoneNumber:String?=null
    var website:String?=null
    var priceLevel:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        onSelectedItemSpinnerListener()
        setContentView(binding.root)
        changeLngButtonListenerConfig()
        changeModeButtonListenerConfig()
        findFoodButtonListenerConfig()

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
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
        } else {
            buildLocationRequest()
            buildLocationCallback()
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        Places.initialize(applicationContext, R.string.google_maps_key.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            }
        } else {
            map.isMyLocationEnabled = true
        }
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener { marker ->
            if(marker != mMarker) {
                if (marker.isInfoWindowShown) {
                    marker.hideInfoWindow()
                } else {
                    marker.showInfoWindow()
                }
                Common.currentResult = currentPlace!!.results!![Integer.parseInt(marker.snippet)]
                mDetails.getDetailPlace(getPlaceDetailUrl(Common.currentResult!!.place_id!!))
                    .enqueue(object : retrofit2.Callback<PlaceDetail> {
                        override fun onResponse(
                            call: Call<PlaceDetail>,
                            response: Response<PlaceDetail>
                        ) {
                            mPlace = response!!.body()
                            name = mPlace!!.result!!.name
                            rating = mPlace!!.result!!.rating.toString()
                            address = mPlace!!.result!!.formatted_address
                            if (mPlace!!.result!!.name == null) "-" else name = mPlace!!.result!!.name
                            if (mPlace!!.result!!.rating.toString() == null) "-" else rating = mPlace!!.result!!.rating.toString()
                            if (mPlace!!.result!!.formatted_address == null) "-" else address = mPlace!!.result!!.formatted_address
                            if (mPlace!!.result!!.formatted_phone_number == null) phoneNumber = "-" else phoneNumber = mPlace!!.result!!.formatted_phone_number
                            if (mPlace!!.result!!.website == null) website = "-" else website = mPlace!!.result!!.website
                            if (mPlace!!.result!!.price_level.toString() == null ) "-" else priceLevel = mPlace!!.result!!.price_level.toString()
                            Log.d("URL_PHONE", "" + name)
                            Log.d("URL_PHONE", "" + rating)
                            Log.d("URL_PHONE", "" + address)
                            Log.d("URL_PHONE", "" + phoneNumber)
                            Log.d("URL_PHONE", "" + website)
                            Log.d("URL_PHONE", "" + priceLevel)
                        }

                        override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                            Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            true
        }
        map.setInfoWindowAdapter(InfoWindowModification(this))
    }

    private fun getPlaceDetailUrl(place_id:String) : String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?placeid=$place_id")
        url.append("&key=AIzaSyCoZwNDKs4JRA3HNZCKmB_c09GH0bLPnEE")
        Log.d("URL_DETAIL", "" + url.toString())
        return url.toString()
    }
    private fun nearByPlace (nextPageToken: String) {
        map.clear()
        var pageToken = ""
        val url = getUrl(latitude,longitude,typePlace,nextPageToken)
        mService.getNearbyPlaces(url)
            .enqueue(object : Callback<MyPlaces> {
                override fun onResponse(call: Call<MyPlaces>?, response: Response<MyPlaces>?) {
                    currentPlace = response!!.body()!!
                    if (response!!.isSuccessful)
                    {
//                        Log.d("URL_TOKIKIK", "" + response.body()!!.results!!.size)
                        for(i in 0 until response.body()!!.results!!.size)
                        {
                            val markerOptions = MarkerOptions()
                            val googlePlace = response.body()!!.results!![i]
                            val lat = googlePlace.geometry!!.location!!.lat
                            val lng = googlePlace.geometry!!.location!!.lng
                            val placeName = googlePlace.name
                            val latLng = LatLng(lat,lng)

                            markerOptions.position(latLng)
                            markerOptions.title(placeName)
                            markerOptions.snippet(i.toString())
                            map!!.addMarker(markerOptions)
                        }
//                        if (response.body()!!.next_page_token != null) {
//                            pageToken = response.body()!!.next_page_token.toString()
////                            Log.d("URL_TOKENNNN", "PageToken "+ pageToken);
//                            nearByPlace(pageToken)
//                        } else {
//                            pageToken = ""
////                            Log.d("URL_TOKENNNN", "PageTokenZE "+ pageToken);
//                        }
                    }
                }
                override fun onFailure(call: Call<MyPlaces>?, t: Throwable) {
                    Toast.makeText(baseContext,""+t!!.message, Toast.LENGTH_SHORT).show()
                    Log.d("URL_DEBUG", ""+ t!!.message);
                }
            })
    }

    private fun getUrl(latitude: Double, longitude: Double, typePlace: String, nextPage: String): String {
        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
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
                mLastLocation = p0!!.locations.get(p0!!.locations.size-1)

                if (mMarker != null) {

                    mMarker!!.remove()
                }
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude,longitude)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("You are here ;-)")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                mMarker = map!!.addMarker(markerOptions)
                val cameraPosition = CameraPosition.Builder()
                    .target(latLng).zoom(16f).build()
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//                map.animateCamera(CameraUpdateFactory.zoomTo(7f))
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),MY_PERMISSION_CODE)
            else
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),MY_PERMISSION_CODE)
            return false
        }
        else
            return true
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            MY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                            map.isMyLocationEnabled = true
                        }
                    }
                } else {
                    Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onSelectedItemSpinnerListener() {
        filterList = resources.getStringArray(R.array.MapFilters)
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterList)
        binding.mapFilterSpinner.adapter = spinnerAdapter
        binding.mapFilterSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View?, position: Int, id: Long) {
                Toast.makeText(this@MapsActivity, "Selected item: "+"" + filterList[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun findFoodButtonListenerConfig() {
        binding.findFoodOnMapButton.setOnClickListener {
            nearByPlace("")
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
                else -> { }
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
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterList)
        binding.mapFilterSpinner.adapter = spinnerAdapter
    }

    private fun changeModeButtonListenerConfig() {

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if(isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.changeModeMapButton.setImageResource(R.drawable.ic_light_mode)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.changeModeMapButton.setImageResource(R.drawable.ic_dark_mode)
        }

        binding.changeModeMapButton.setOnClickListener {

            if (isNightModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                binding.changeModeMapButton.setImageResource(R.drawable.ic_dark_mode)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                binding.changeModeMapButton.setImageResource(R.drawable.ic_light_mode)
            }
            sharedPrefsEdit.apply()
        }
    }
}