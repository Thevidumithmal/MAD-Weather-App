package com.example.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.data.forecastModels.ForecastData
import com.example.weatherapp.databinding.RecyclearItemLayoutBinding
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter
import org.threeten.bp.LocalDateTime;
import java.text.SimpleDateFormat

class RecyclearAdapter(private val forecasdArray: ArrayList<ForecastData>) : RecyclerView.Adapter<RecyclearAdapter.ViewHolder>() {

    class ViewHolder(val binding:RecyclearItemLayoutBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclearAdapter.ViewHolder {
      return ViewHolder(RecyclearItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecyclearAdapter.ViewHolder, position: Int) {
        val currentItem= forecasdArray[position]
        holder.binding.apply {
            val imageIcon = currentItem.weather[0].icon
            val imageUrl = "https://openweathermap.org/img/w/$imageIcon.png"

            Picasso.get().load(imageUrl).into(imgItem)

            tvItemTemp.text= "${currentItem.main.temp} °C"
            tvItemStatus.text = "${currentItem.weather[0].description}"
            tvItemTime.text= displayTime(currentItem.dt_txt)

        }
    }

    private fun displayTime(dtTxt: String): CharSequence? {
        val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val output = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val dateTime = input.parse(dtTxt)
        return output.format(dateTime)

    }

    override fun getItemCount(): Int {
       return forecasdArray.size
    }
}