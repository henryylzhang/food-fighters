package edu.washington.zhang007.foodfighters

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yelp.fusion.client.connection.YelpFusionApiFactory
import com.yelp.fusion.client.models.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LandingActivity : AppCompatActivity() {

    val TAG = "LandingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val apiFactory = YelpFusionApiFactory()
        val yelpFusionApi = apiFactory.createAPI(getString(R.string.yelp_key))
        val params: HashMap<String, String> = hashMapOf()

        params.put("latitude", "47.6553")
        params.put("longitude", "-122.3035")
        params.put("radius", "1000")
        params.put("limit", "3")

        val callback = object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val searchResponse = response.body()

                val totalItems = searchResponse.total
                Log.i(TAG, totalItems.toString())

                val businesses = searchResponse.businesses

                for (i in 0..(totalItems - 1)) {
                    Log.i(TAG, i.toString() + " = " + businesses.get(i).name)
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
