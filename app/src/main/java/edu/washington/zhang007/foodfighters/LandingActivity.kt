package edu.washington.zhang007.foodfighters

import android.content.Intent
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.fusion.client.models.SearchResponse
import kotlinx.android.synthetic.main.activity_landing.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random


class LandingActivity : AppCompatActivity() {

    private val TAG = "LandingActivity"
    private val yelp_limit = "50" // maximum # of restaurants returned per call
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //------------------------------------------------------------------
    //   Activity Overrides
    //------------------------------------------------------------------

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getInitialLocation()

        val settingsButton = btn_settings

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)

            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            MapsActivity.MY_PERMISSION_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //------------------------------------------------------------------
    //   Private Method
    //------------------------------------------------------------------

    /*
    Generates a random number between 'endInclusive' and 'start'
     */
    private fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) +  start

    /*
    Checks location permission
     */
    private fun checkLocationPermission() : Boolean {
        return if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MapsActivity.MY_PERMISSION_CODE)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), MapsActivity.MY_PERMISSION_CODE)
            }
            false
        } else {
            true
        }
    }

    private fun getInitialLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkLocationPermission()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Save the lat and long locally.
                    Preferences.latitude = location.latitude
                    Preferences.longitude = location.longitude

                    Log.i(TAG, "In memory: [ ${Preferences.latitude}, ${Preferences.longitude} ]")
                } else {
                    Log.e(TAG, "Successful but there is no location")
                }

            }
        } else {
            Log.e(TAG, "We don't have location permissions")
        }
    }

    //------------------------------------------------------------------
    //   Yelp Call / Callback
    //------------------------------------------------------------------

    private fun yelpSearch() {
        if (Preferences.latitude == null && Preferences.longitude == null) {
            Log.e(TAG, "We have no location so no search for you")
            return
        }

        val apiFactory = YelpFusionApiFactory()
        val yelpFusionApi = apiFactory.createAPI("Y-Oa9oQR0sV6OBB4FX7ItzltmQCgP5sqFL9Y0xD-F5uJ39ci53_lNgzD2nrZyxa3FkeAboQaDO833ufEm0fVsnq5wqIaGME6OSVZ6r69TI6Unz4rBFVgI_heLHP8WnYx")
        val params: HashMap<String, String> = hashMapOf()

        params.put("latitude", "${Preferences.latitude}") // will be user set
        params.put("longitude", "${Preferences.longitude}") // will be user set
        params.put("radius", Preferences.radius) // will be user set
        params.put("limit", yelp_limit) // set to maximum per call

        val callback = object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val searchResponse = response.body()
                val restaurantChoices = Preferences.numChoices
                val businesses = searchResponse.businesses

                for (i in 0..(restaurantChoices.toInt() - 1)) {
                    val j = (0 until businesses.size).random()
                    Log.i(TAG, i.toString() + " restnum=" + j.toString() + " = " + businesses.get(j).name)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        }

        val call = yelpFusionApi.getBusinessSearch(params)
        call.enqueue(callback)
    }
}
