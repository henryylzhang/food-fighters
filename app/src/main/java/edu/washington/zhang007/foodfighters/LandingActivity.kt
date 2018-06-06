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
        val yelpFusionApi = apiFactory.createAPI(R.string.yelp_key.toString())

        val params: HashMap<String, String> = hashMapOf()


        params.put("latitude", "47.6553")
        params.put("longitude", "122.3035")
        params.put("radius", "4000")
        params.put("limit", "5")


        val callback = object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                val searchResponse = response.body()

                val totalItems = searchResponse.total

                val businesses = searchResponse.businesses

                for (i in 0..totalItems) {
                    Log.i(TAG, businesses.get(i).name)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(TAG, "Something terrible has happened")
            }
        }

        val call = yelpFusionApi.getBusinessSearch(params)
        call.enqueue(callback)
    }
}
