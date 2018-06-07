package edu.washington.zhang007.foodfighters

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.yelp.fusion.client.models.Business
import kotlinx.android.synthetic.main.activity_restaurant_details.*

class RestaurantDetailsActivity : AppCompatActivity() {

    private var MY_PERMISSIONS_REQUEST_CALL_PHONE = 0
    private var MY_PERMISSIONS_REQUEST_SEND_SMS = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        val restName = text_restaurantName
        val rating = text_rating
        val phone = text_phone
        val hours = text_businessHours

        val business = intent.getSerializableExtra("Business") as Business

        if (business == null) { println("YO I'M NULL") }
        restName.text = business.name
        rating.text = business.rating.toString() + " Stars"
        phone.text = business.phone
        // hours.text = business.hours.toString()

        phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.setData(Uri.parse("tel:" + phone.text))
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        MY_PERMISSIONS_REQUEST_CALL_PHONE)
                startActivity(intent)
            } else {
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
