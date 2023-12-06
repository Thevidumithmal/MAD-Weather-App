package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.forecastModels.ForecastData
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.databinding.BottomLayoutBinding
import com.example.weatherapp.utils.RetrofitInstance
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var sheetLayoutBinding: BottomLayoutBinding

    private lateinit var dialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        sheetLayoutBinding= BottomLayoutBinding.inflate(layoutInflater)
        dialog= BottomSheetDialog(this,R.style.BottomSheetTheme)
       dialog.setContentView(sheetLayoutBinding.root)
        setContentView(binding.root)

        getCurrentLocationWeather()

        binding.btnForecast.setOnClickListener{

            openDialog()
        }


    }

    //forecast--------------------------------------------------------------------------------------------------
    private fun openDialog() {
        getForecast()

        sheetLayoutBinding.rvForecast.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@MainActivity,1, RecyclerView.HORIZONTAL,false)
        }

        //pass data to recyler adapter----
        dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation
        dialog.show()
    }

    private fun getForecast() {

        GlobalScope.launch(Dispatchers.IO) {
            val response = try {
                RetrofitInstance.api.getForecast(
                    "colombo",
                    "metric",
                    applicationContext.getString(R.string.api_key)
                )

            } catch (e: IOException) {
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch

            } catch (e: HttpException) {
                Toast.makeText(applicationContext, "http error ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch

            }
            if (response.isSuccessful && response.body() != null) {
                withContext(Dispatchers.Main) {

                    val data = response.body()!!

                    var forecasdArray = arrayListOf<ForecastData>()
                    forecasdArray=data.list as ArrayList<ForecastData>

                    val adapter= RecyclearAdapter(forecasdArray)
                    sheetLayoutBinding.rvForecast.adapter=adapter
                    sheetLayoutBinding.tvSheet.text= "Five days forecast in ${data.city.name}"

                }
            }

        }

    }

    private fun getCurrentLocationWeather() {
        GlobalScope.launch(Dispatchers.IO){
            val response = try {
                RetrofitInstance.api.getCurrentLocationWeather("colombo","metric",applicationContext.getString(R.string.api_key))

            } catch (e:IOException){
                Toast.makeText(applicationContext, "app error ${e.message}", Toast.LENGTH_SHORT).show()
                return@launch

            }catch (e:HttpException){
                Toast.makeText(applicationContext, "http error ${e.message}", Toast.LENGTH_SHORT).show()
                return@launch

            }
            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main){

                    val data = response.body()!!

                    // get icon id----------------
                    val iconId= data.weather[0].icon

                    // weather image change---------------
                    val imageUrl="https://openweathermap.org/img/w/$iconId.png"

                    //set image for imgweather-----------
                    Picasso.get().load(imageUrl).into(binding.imgWeather)

                    // set sunrise time (format) -------
                    binding.tvSunrise.text=
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format( data.sys.sunrise * 1000)

                    // set sunset time (format) -------
                    binding.tvSunset.text=
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format( data.sys.sunset * 1000)

                    // weather status change (clear sky) -----------
                    // wind change , location change , tem change , feel like change -------
                    // humidity , pressure-----
                    binding.apply {
                        tvStatus.text= data.weather[0].description
                        tvWind.text = "${data.wind.speed.toString()} KM/H"
                        tvLocation.text = "${data.name}  ${data.sys.country}"
                        tvTemp.text= "${data.main.temp.toInt()} °C"
                        tvFeelsLike.text= "Feel like: ${data.main.feels_like.toInt()} °C"
                        tvMaxTemp.text= "Mintem: \n${data.main.temp_min.toInt()} °C"
                        tvMinTemp.text= "Maxtem: \n${data.main.temp_max.toInt()} °C"
                        tvHumidity.text= "${data.main.humidity} %"
                        tvUpdateTime.text= "Last Update: ${
                            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format( data.dt * 1000)
                        }"

                    }

                }
            }
        }
    }
}