package edu.washington.zhang007.foodfighters

import android.content.Intent
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.fusion.client.models.Business
import edu.washington.zhang007.foodfighters.model.BusinessData
import edu.washington.zhang007.foodfighters.model.HenryCallback
import edu.washington.zhang007.foodfighters.model.Preferences
import edu.washington.zhang007.foodfighters.model.YelpRequestCallBack
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity(), HenryCallback {

    private val TAG = "LandingActivity"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var loadingText: TextView

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
        val searchButton = btn_search
        loadingText = tv_loading

        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        searchButton.setOnClickListener {
            yelpSearch()
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

        loadingText.text = "Searching..."

        val apiFactory = YelpFusionApiFactory()
        val yelpFusionApi = apiFactory.createAPI(getString(R.string.yelp_key))

        val callback = YelpRequestCallBack(Preferences.latitude!!, Preferences.longitude!!,
                Preferences.radius, this)

        val call = yelpFusionApi.getBusinessSearch(callback.params)
        call.enqueue(callback)
    }

    override fun henrySuccess() {
        // Do stuff on UI here.
        loadingText.text = "Done! Success!"

        Log.i(TAG, "BusinessData size: ${BusinessData.size()}")

        val businessHolder: ArrayList<Business> = arrayListOf()

        //Fetches a random business
        for (i in 0 until Preferences.numChoices.toInt()) {
            businessHolder.add(BusinessData.getRandom())
        }

        val intent = Intent(this, MapsActivity::class.java)

        intent.putExtra("QQ", businessHolder)

        startActivity(intent)
    }

    override fun henryFailure() {
        loadingText.text = "Failed"
    }
}
