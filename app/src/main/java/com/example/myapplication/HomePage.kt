package com.example.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HomePage : AppCompatActivity() {

    lateinit var textView: TextView
    lateinit var button : Button
    lateinit var  lbl_current_status_textview:TextView
    lateinit var  lbl_current_temperature:TextView
    private lateinit var imgIcon: ImageView
    lateinit var lbl_last_updated_date:TextView

    private val apiKey="62cafbcdd896507bde9ac60b88b3bd63"
    private var progressDialog: ProgressDialog? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        textView= findViewById(R.id.textView4)
        button= findViewById(R.id.move2ndPage)
        lbl_current_status_textview=findViewById(R.id.lbl_current_status_textview)
        lbl_current_temperature=findViewById(R.id.lbl_current_temperature)
        imgIcon = findViewById(R.id.imgIcon)
        lbl_last_updated_date= findViewById(R.id.lbl_last_updated_date)

        val searchQuery = intent.getStringExtra("selectedValue")?: "Default Value"
        textView.text = "$searchQuery"
        loadWeatherInfo(searchQuery)


        //get Today Day and TIme
        val todayDate = getCurrentDate()
        println("Today's Date: $todayDate")
        val todayDateString=getCurrentDateString()
        lbl_last_updated_date.text="$todayDate | $todayDateString"



        button.setOnClickListener {
            // Move to the second page and pass the selected value
            val intent = Intent(this, HomePageV2::class.java)
            intent.putExtra("selectedValue", searchQuery)
            startActivity(intent)
        }

    }
    private fun loadWeatherInfo(cityname: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityname&units=metric&appid=$apiKey"
        showProgressDialog("Loading")
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { data ->
                try {
                    val description = data.getJSONArray("weather").getJSONObject(0).getString("description")
                    lbl_current_status_textview.text = "$description"

                    lbl_current_temperature.text = "" + data.getJSONObject("main").getInt("temp")+"°C"

//                    lblPressure.text = "Pressure: " + data.getJSONObject("main").getString("pressure")
//                    lblHumidity.text = "Humidity: " + data.getJSONObject("main").getString("humidity")
//                    lblWindSpeed.text = "Wind Speed: " + data.getJSONObject("wind").getString("speed")

                    val iconCode = data.getJSONArray("weather").getJSONObject(0).getString("icon")
//                    val imgIconUrl = "https://openweathermap.org/img/w/$iconCode.png"
//
//                    // Use Picasso to load the image into imgIcon
//                    Picasso.get().load(imgIconUrl).into(imgIcon)
                } catch (e: Exception) {
                    Log.e("Error", e.toString())
                }

                dismissProgressDialog()
            },
            { error ->
                Log.e("Response", error.toString())
                // Handle error here
                dismissProgressDialog()
                showErrorToast("Error fetching weather data")
            })

        Volley.newRequestQueue(this).add(request)
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateString(): String {
        // Get the current date
        val currentDate = LocalDate.now()
        val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        // Format the date if needed
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        return dayOfWeek
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        // Get the current date
        val currentDate = LocalDate.now()
        // Format the date if needed
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)

        return formattedDate
    }

}

