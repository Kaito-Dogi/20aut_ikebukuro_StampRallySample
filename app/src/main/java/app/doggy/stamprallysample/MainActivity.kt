package app.doggy.stamprallysample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationListener {

    // lateinit: late initialize to avoid checking null
    private lateinit var locationManager: LocationManager

    var locationInfo: MutableList<Double> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1000)
        } else {
            locationStart()

            if (::locationManager.isInitialized) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        50f,
                        this)
            }

        }

        //写真を押した時の処理。
        //東京タワー 35.658824543225336, 139.7454329065958
        tokyoTowerImage.setOnClickListener {
            if (locationInfo[0] in 35.64..35.66 && locationInfo[1] in 139.73..139.75) {
                tokyoTowerImage.setImageResource(R.drawable.tokyo_tower)
                Toast.makeText(this@MainActivity, "ここは東京タワーだ！", Toast.LENGTH_LONG).show()
                tokyoTowerImage.isEnabled = false
            } else {
                Toast.makeText(this@MainActivity, "ここは東京タワーではない…", Toast.LENGTH_LONG).show()
            }
        }

        //雷門 35.71126371582462, 139.7963434967745
        kaminariMonImage.setOnClickListener {
            if (locationInfo[0] in 35.70..35.72 && locationInfo[1] in 139.78..139.80) {
                tokyoTowerImage.setImageResource(R.drawable.kaminari_mon)
                Toast.makeText(this@MainActivity, "ここは雷門だ！", Toast.LENGTH_LONG).show()
                kaminariMonImage.isEnabled = false
            } else {
                Toast.makeText(this@MainActivity, "ここは雷門ではない…", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun locationStart() {
        Log.d("debug", "locationStart()")

        // Instances of LocationManager class must be obtained using Context.getSystemService(Class)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled")
        } else {
            // to prompt setting up GPS
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
            Log.d("debug", "not gpsEnable, startActivity")
        }

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)

            Log.d("debug", "checkSelfPermission false")
            return
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                50f,
                this)

    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true")

                locationStart()

            } else {
                // それでも拒否された時の対応
                val toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        /* API 29以降非推奨
        when (status) {
            LocationProvider.AVAILABLE ->
                Log.d("debug", "LocationProvider.AVAILABLE")
            LocationProvider.OUT_OF_SERVICE ->
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE ->
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE")
        }
         */
    }

    override fun onLocationChanged(location: Location) {
        // Latitude
        val latitudeTextView = findViewById<TextView>(R.id.latitudeTextView)
        val str1 = "北緯:" + location.getLatitude() + "度"
        latitudeTextView.text = str1
        locationInfo.add(location.getLatitude())

        // Longitude
        val longitudeTextView = findViewById<TextView>(R.id.longitudeTextView)
        val str2 = "東経:" + location.getLongitude() + "度"
        longitudeTextView.text = str2
        locationInfo.add(location.getLongitude())

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

}