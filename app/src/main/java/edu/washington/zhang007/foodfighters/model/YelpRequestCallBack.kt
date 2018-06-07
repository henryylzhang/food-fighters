package edu.washington.zhang007.foodfighters.model

import android.util.Log
import com.yelp.fusion.client.models.SearchResponse
import edu.washington.zhang007.foodfighters.util.random
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YelpRequestCallBack(val lat: Double, val long: Double,
                          val radius: String, private val callback: HenryCallback?): Callback<SearchResponse> {

    private val TAG = "YelpRequestCallBack"
    private val yelp_limit = "50" // maximum # of restaurants returned per call

    val params: HashMap<String, String> = hashMapOf()

    init {
        params.put("latitude", "$lat")
        params.put("longitude", "$long")
        params.put("radius", radius)
        params.put("limit", yelp_limit)
    }

    //------------------------------------------------------------------
    //   Callback Overrides
    //------------------------------------------------------------------

    override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
        val searchResponse = response.body()
        val restaurantChoices = Preferences.numChoices
        val businesses = searchResponse.businesses

        for (i in 0..(restaurantChoices.toInt() - 1)) {
            val j = (0 until businesses.size).random()
            Log.i(TAG, i.toString() + " restnum=" + j.toString() + " = " + businesses.get(j).name)
        }

        callback?.henrySuccess()
    }

    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
        Log.e(TAG, t.toString())
        callback?.henryFailure()
    }
}

interface HenryCallback {
    fun henrySuccess()
    fun henryFailure()
}