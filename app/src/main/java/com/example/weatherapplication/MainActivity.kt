package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        fetchWeatherData("Dera ismail khan")
        // Call the function to fetch weather data

        searchCity()
    }
    private fun searchCity(){
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }
    private fun fetchWeatherData(cityname: String) {

        val retrofit = Retrofit.Builder()

            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityname, "bc93b5e3047bd3fe1b04c5ce79a48bdf", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxtemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min

                    binding.textViewtemp.text = "$temperature℃"
                    binding.textViewCondition.text = condition
                    binding.textViewmaxtemp.text = "$maxtemp ℃"
                    binding.textViewmintemp.text = "$mintemp ℃"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windspeed m/s"
                    binding.sea.text = "$sealevel hpa"
                    binding.condition.text = condition
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.textViewdate.text = date()
                    binding.textViewday.text = dayName()
                    binding.textViewcityname.text ="$cityname"

                    ChangeImageAccordingtoWeatherCondition(condition)
                }
            }

            private fun ChangeImageAccordingtoWeatherCondition(condition :String) {

                when(condition){
                   "Clear sky","Sunny","Clear"->{
                       binding.root.setBackgroundResource(R.drawable.sunny_background)
                       binding.lottieAnimationView2.setAnimation(R.raw.sun)
                   }
                    "Partly Clouds","Clouds","overcast","Mist","Foggy","Smoge","Smoke"->{
                        binding.root.setBackgroundResource(R.drawable.colud_background)
                        binding.lottieAnimationView2.setAnimation(R.raw.cloud)
                    }
                    "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                        binding.root.setBackgroundResource(R.drawable.rain_background)
                        binding.lottieAnimationView2.setAnimation(R.raw.rain)
                    }
                    "Light Snow","Heavy Snow","Moderate Snow","Blizzard"->{
                        binding.root.setBackgroundResource(R.drawable.snow_background)
                        binding.lottieAnimationView2.setAnimation(R.raw.snow)
                    }
else->{
    binding.root.setBackgroundResource(R.drawable.sunny_background)
    binding.lottieAnimationView2.setAnimation(R.raw.sun)

                }

                }
                   binding.lottieAnimationView2.playAnimation()
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })
    }

    private fun dayName(): String {
        val sdt = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdt.format(Date())
    }
    private fun time(timestamp :Long): String {
        val sdt = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdt.format(Date(timestamp*1000))
    }

    private fun date(): String {
        val sdt = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdt.format(Date())
    }
}
