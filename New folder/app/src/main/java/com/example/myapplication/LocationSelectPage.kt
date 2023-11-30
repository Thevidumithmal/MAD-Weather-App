package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.picasso.Picasso
import java.io.IOException

class LocationSelectPage : AppCompatActivity() {

    private lateinit var txtCityName: TextView
    private lateinit var btnCityFind : Button

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val data = arrayOf("Suggest Locations","Colombo", "Kandy", "Nugegoda")

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select_page)

        txtCityName= findViewById(R.id.txtCityName)
        btnCityFind= findViewById(R.id.btnCityFind)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val searchButton = findViewById<Button>(R.id.searchButton)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        requestLocationPermission()
        getCurrentLocation()

        // Search Bar Operations
        searchButton.setOnClickListener {
            val query = searchView.query.toString().trim()

            if (query.isNotEmpty()) {
                val intent = Intent(this, HomePage::class.java)
                intent.putExtra("selectedValue", query)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Update TextView with live location using Geocoder
                        updateLocationTextView(location.latitude, location.longitude)

                    } else {
                        dismissProgressDialog()
                        showErrorToast("Location not available")
                    }
                }
                .addOnFailureListener { e ->
                    dismissProgressDialog()
                    Log.e("Location", "Error getting location", e)
                    showErrorToast("Error getting location") }
        } catch (e: SecurityException) {
            dismissProgressDialog()
            Log.e("Location", "Security exception: ${e.message}")
            showErrorToast("Location permission denied")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationTextView(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this)
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            null
        }

        val cityName = addresses?.firstOrNull()?.locality

        if (cityName != null) {
            txtCityName.text = "$cityName"
        } else {
            txtCityName.text = "$latitude, Lon: $longitude"
        }
        btnCityFind.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("selectedValue", cityName)
            startActivity(intent)
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

}