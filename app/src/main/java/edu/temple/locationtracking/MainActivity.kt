package edu.temple.locationtracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    val locationManager : LocationManager by lazy {
        getSystemService(LocationManager::class.java)
    }

    lateinit var locationListener: LocationListener

    var previousLocation : Location? = null
    var distanceTraveled = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)

        if (!permissionGranted()) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 123)
        }

        locationListener = LocationListener {
            if (previousLocation != null) {
                distanceTraveled += it.distanceTo(previousLocation)
                textView.text = distanceTraveled.toString()
            }
            previousLocation = it
        }

    }

    @SuppressLint("MissingPermission")
    private fun doGPSStuff() {
        if (permissionGranted())
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000.toLong(), 5f, locationListener)
    }

    private fun permissionGranted () : Boolean {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        super.onResume()
        doGPSStuff()
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }

    }
}