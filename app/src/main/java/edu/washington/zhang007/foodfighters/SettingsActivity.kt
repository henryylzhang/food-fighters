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
        val maxDist = editText_maxDistance
        val save = btn_savePrefs

        var numChoicesString = Preferences.numChoices
        var radiusString = Preferences.radius

        // set up choices spinner
        val choicesAdapter: ArrayAdapter<CharSequence> =
                ArrayAdapter.createFromResource(this, R.array.num_restaurants, android.R.layout.simple_spinner_item)
        choicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        numChoices.adapter = choicesAdapter

        numChoices.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                numChoicesString = numChoices.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // set up radius
        maxDist.setText(radiusString) // save distance from before

        maxDist.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var dist = maxDist.text.toString()

                if(maxDist.text.isEmpty()) {
                    maxDist.setText("1000")
                    maxDist.setSelection(dist.length) // set cursor at end
                } else if (dist.toInt() > 40000) { // max distance for Yelp API call
                    maxDist.setText("40000")
                }

                radiusString = dist
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        save.setOnClickListener {
            Preferences.numChoices = numChoicesString
            Preferences.radius = radiusString

            Toast.makeText(this, "Preferences Saved!", Toast.LENGTH_SHORT).show()
        }
    }
}
