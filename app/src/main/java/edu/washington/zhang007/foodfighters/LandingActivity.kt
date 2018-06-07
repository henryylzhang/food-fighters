package edu.washington.zhang007.foodfighters

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.fusion.client.models.SearchResponse
import kotlinx.android.synthetic.main.activity_landing.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random


class LandingActivity : AppCompatActivity() {

    val TAG = "LandingActivity"
    val yelp_limit = "50" // maximum # of restaurants returned per call

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val apiFactory = YelpFusionApiFactory()
        val yelpFusionApi = apiFactory.createAPI(getString(R.string.yelp_key))
        val params: HashMap<String, String> = hashMapOf()
        val prefs = Preferences()

        params.put("latitude", "47.6553") // will be user set
        params.put("longitude", "-122.3035") // will be user set
        params.put("radius", prefs.radius) // will be user set
        params.put("limit", yelp_limit) // set to maximum per call

        val callback = object : Callback<SearchResponse> {


            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val searchResponse = response.body()

                val restaurantChoices = prefs.numChoices // this will be set to user preference later on

                val businesses = searchResponse.businesses

                for (i in 0..(restaurantChoices.toInt() - 1)) {
                    val j = (0..50).random() // upper limit should be as many as I can pull w/ offset?
                    Log.i(TAG, i.toString() + " restnum=" + j.toString() + " = " + businesses.get(j).name)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, t.toString())
            }
        }

        val call = yelpFusionApi.getBusinessSearch(params)
        call.enqueue(callback)

        val prefsButton = btn_prefs

        prefsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)

            startActivity(intent)
        }
    }

    fun ClosedRange<Int>.random() =
            Random().nextInt(endInclusive - start) +  start
}
