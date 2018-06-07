package edu.washington.zhang007.foodfighters.model

import com.yelp.fusion.client.models.Business
import edu.washington.zhang007.foodfighters.util.random

/**
 * Created by Nick on: 6/7/18.
 * Last updated on: 6/7/18
 *
 * TODO: Add class header comment
 */

object BusinessData {
    private var data: List<Business> = ArrayList()

    fun replaceData(newData: List<Business> ) {
        data = newData
    }

    fun size(): Int {
        return data.size
    }

    fun get(index: Int): Business {
        return data[index]
    }

    fun getRandom(): Business {
        return data[(0 until data.size).random()]
    }
}