package edu.washington.zhang007.foodfighters

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import edu.washington.zhang007.foodfighters.model.Preferences
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val numChoices = spinner_numSuggestions

        val choicesAdapter: ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(this, R.array.num_restaurants, android.R.layout.simple_spinner_item)
        choicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        numChoices.adapter = choicesAdapter

        numChoices.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val num = numChoices.getItemAtPosition(position).toString()

                Preferences.numChoices = num
                Toast.makeText(this@SettingsActivity, "Number of Choices Updated!", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val maxDist = editText_maxDistance

        maxDist.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var dist = maxDist.text.toString()

                if(maxDist.text.isEmpty()) {
                    maxDist.setText("1000")
                } else if (dist.toInt() > 40000) { // max distance for Yelp API call
                    maxDist.setText("40000")

                    dist = "40000"
                }

                Preferences.radius = dist
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }
}
