package com.example.android.addressdhundo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap

    private lateinit var locationManager: LocationManager

    private val PERMISSION_REQUEST_CODE: Int = 1

    private lateinit var geocoder: Geocoder
    private lateinit var address: List<Address>

    private lateinit var alertDialog: AlertDialog.Builder

    private lateinit var view: View
    private lateinit var tvAddress: TextView
    private lateinit var btnShare: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

        geocoder = Geocoder(this, Locale.getDefault())
    }

    override fun onLocationChanged(location: Location) {

        try {
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            if (!address.isNullOrEmpty()) {
                mMap.clear()
                updateMap(LatLng(location.latitude, location.longitude), "${R.string.current_location}", address)
            } else {
                Snackbar.make(findViewById(R.id.parent_layout), R.string.location_not_found, Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Snackbar.make(findViewById(R.id.parent_layout), R.string.error_msg, Snackbar.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        mMap.isBuildingsEnabled = true

        // Add a marker in Delhi and move the camera
        val delhi = LatLng(28.6, 77.2)
        mMap.addMarker(MarkerOptions()
            .position(delhi)
            .title("Delhi"))

        mMap.moveCamera(CameraUpdateFactory
            .newLatLngZoom(delhi, 20f))

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0f, this)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onMapLongClick(latlng: LatLng) {

        try {
            address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1)

            if (!address.isNullOrEmpty()) {
                updateMap(latlng, address[0].postalCode, address)
            } else {
                Snackbar.make(findViewById(R.id.parent_layout), R.string.location_not_traced, Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Snackbar.make(findViewById(R.id.parent_layout), R.string.error_msg, Snackbar.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun buildDialog(address: String){
        view = layoutInflater.inflate(R.layout.dialog_address_details, null)

        tvAddress = view.findViewById(R.id.tvAddress)
        btnShare = view.findViewById(R.id.btnShare)

        tvAddress.text = address

        btnShare.setOnClickListener {
            var intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, "${R.string.dialog_title}")
            intent.putExtra(Intent.EXTRA_TEXT, address)
            intent.type = "text/plain"
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        alertDialog = AlertDialog.Builder(this)
        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun updateMap(latlng: LatLng, title: String, address: List<Address>){
        mMap.addMarker(MarkerOptions()
                .position(latlng)
                .title(title))

        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(latlng, 20f))

        buildDialog(address[0].getAddressLine(0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0f, this)
                    }
                } else {
                    Snackbar.make(findViewById(R.id.parent_layout), R.string.permission_denied, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}