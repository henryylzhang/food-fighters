package edu.washington.zhang007.foodfighters

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.SmsManager
import android.widget.Toast
import com.yelp.fusion.client.models.Business
import kotlinx.android.synthetic.main.activity_restaurant_details.*

class RestaurantDetailsActivity : AppCompatActivity() {

    private var MY_PERMISSIONS_REQUEST_CALL_PHONE = 0
    private var MY_PERMISSIONS_REQUEST_SEND_SMS = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        // check for SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_SEND_SMS)
        }

            val restName = text_restaurantName
            val rating = text_rating
            val phone = text_phone

            val sendText = btn_text

            val business = intent.getSerializableExtra("Business") as Business

            restName.text = business.name
            rating.text = business.rating.toString() + " Stars"
            phone.text = business.phone

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

            btn_text.setOnClickListener {
                val sms = SmsManager.getDefault()

                val phoneNumber = phone.text.toString()
                val message = StringBuffer()
                message.append("Join me at ")
                message.append(restName.text.toString())
                message.append("! ")
                message.append("http://maps.google.com?q=")
                message.append(business.coordinates.latitude.toString())
                message.append(",")
                message.append(business.coordinates.longitude.toString())

                sms.sendTextMessage(phoneNumber, null, message.toString(), null, null)
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
