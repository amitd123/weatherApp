package com.amit.weatherapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amit.weatherapp.R
import com.amit.weatherapp.databinding.ActivityWeatherBinding
import com.amit.weatherapp.model.WeatherInfoShowModel
import com.amit.weatherapp.model.WeatherInfoShowModelImpl
import com.amit.weatherapp.model.data_class.City
import com.amit.weatherapp.model.data_class.WeatherData
import com.amit.weatherapp.utils.convertToListOfCityName
import com.bumptech.glide.Glide


class WeatherActivity : AppCompatActivity() {

    private lateinit var model: WeatherInfoShowModel
    private lateinit var viewModel: WeatherInfoViewModel
    private lateinit var binding: ActivityWeatherBinding


    private var cityList: MutableList<City> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather)

        // initialize model. (I know we should not initialize model in View. But for simplicity...)
        model = WeatherInfoShowModelImpl(applicationContext)
        // initialize ViewModel
        viewModel = ViewModelProviders.of(this).get(WeatherInfoViewModel::class.java)

        // set LiveData and View click listeners before the call for data fetching
        setLiveDataListeners()
        setViewClickListener()

        /**
         * Fetch city list when Activity open.
         * It's not a very good way that, passing model in every methods of ViewModel. For the sake
         * of simplicity I did so. In real production level App, we can inject out model to ViewModel
         * as a parameter by any dependency injection library like Dagger.
         */
        viewModel.getCityList(model)
    }

    private fun setViewClickListener() {
        // View Weather button click listener
        binding.layoutInput.btnViewWeather.setOnClickListener {
            val lat = cityList[binding.layoutInput.spinner.selectedItemPosition].lat
            val longitude = cityList[binding.layoutInput.spinner.selectedItemPosition].long
            viewModel.getWeatherInfo(lat,longitude, model) // fetch weather info
        }
    }

    private fun setLiveDataListeners() {

        /**
         * When ViewModel PUSH city list to LiveData then this `onChanged()`‍ method will be called.
         * Here we subscribe the LiveData of City list. We don't pull city list from ViewModel.
         * We subscribe to the data source for city list. When LiveData of city list is updated
         * inside ViewModel, below onChanged() method will triggered instantly.
         * City list is fetching from a small local JSON file. So we don't need any ProgressBar here.
         *
         * For better understanding, I didn't use lambda in this method call. Rather thant lambda I
         * implement `Observer` interface in general format. Hope you will understand the inline
         * implementation of `Observer` interface. Rest of the `observe()` method, I've used lambda
         * to short the code.
         */
        viewModel.cityListLiveData.observe(this, object : Observer<MutableList<City>>{
            override fun onChanged(cities: MutableList<City>) {
                setCityListSpinner(cities)
            }
        })

        /**
         * If ViewModel failed to fetch City list from data source, this LiveData will be triggered.
         * I know it's not good to make separate LiveData both for Success and Failure, but for sake
         * of simplification I did it. We can handle all of our errors from our Activity or Fragment
         * Base classes. Another way is: using a Generic wrapper class where you can set the success
         * or failure status for any types of data model.
         *
         * Here I've used lambda expression to implement Observer interface in second parameter.
         */
        viewModel.cityListFailureLiveData.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })

        /**
         * ProgressBar visibility will be handled by this LiveData. ViewModel decides when Activity
         * should show ProgressBar and when hide.
         *
         * Here I've used lambda expression to implement Observer interface in second parameter.
         */
        viewModel.progressBarLiveData.observe(this, Observer { isShowLoader ->
            if (isShowLoader)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        })

        /**
         * This method will be triggered when ViewModel successfully receive WeatherData from our
         * data source (I mean Model). Activity just observing (subscribing) this LiveData for showing
         * weather information on UI. ViewModel receives Weather data API response from Model via
         * Callback method of Model. Then ViewModel apply some business logic and manipulate data.
         * Finally ViewModel PUSH WeatherData to `weatherInfoLiveData`. After PUSHING into it, below
         * method triggered instantly! Then we set the data on UI.
         *
         * Here I've used lambda expression to implement Observer interface in second parameter.
         */
        viewModel.weatherInfoLiveData.observe(this, Observer { weatherData ->
            if (weatherData != null) {
                setWeatherInfo(weatherData)
            }
        })

        /**
         * If ViewModel faces any error during Weather Info fetching API call by Model, then PUSH the
         * error message into `weatherInfoFailureLiveData`. After that, this method will be triggered.
         * Then we will hide the output view and show error message on UI.
         *
         * Here I've used lambda expression to implement Observer interface in second parameter.
         */
        viewModel.weatherInfoFailureLiveData.observe(this, Observer { errorMessage ->
            binding.outputGroup.visibility = View.GONE
            binding.tvErrorMessage.visibility = View.VISIBLE
            binding.tvErrorMessage.text = errorMessage
        })
    }

    private fun setCityListSpinner(cityList: MutableList<City>) {
        this.cityList = cityList

        val arrayAdapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            this.cityList.convertToListOfCityName()
        )

        binding.layoutInput.spinner.adapter = arrayAdapter
    }

    private fun setWeatherInfo(weatherData: WeatherData) {
        binding.outputGroup.visibility = View.VISIBLE
        binding.tvErrorMessage.visibility = View.GONE

        binding.layoutWeatherBasic.tvDateTime.text = weatherData.dateTime
        binding.layoutWeatherBasic.tvTemperature.text = weatherData.temperature
        binding.layoutWeatherBasic.tvCityCountry.text = weatherData.cityAndCountry
        Glide.with(this).load(weatherData.weatherConditionIconUrl).into(binding.layoutWeatherBasic.ivWeatherCondition)
        binding.layoutWeatherBasic.tvWeatherCondition.text = weatherData.weatherConditionIconDescription

        binding.layoutWeatherAdditional.tvHumidityValue.text = weatherData.humidity
        binding.layoutWeatherAdditional.tvPressureValue.text = weatherData.pressure
        binding.layoutWeatherAdditional.tvVisibilityValue.text = weatherData.visibility

        binding.layoutSunsetSunrise.tvSunriseTime.text = weatherData.sunrise
        binding.layoutSunsetSunrise.tvSunriseTime.text = weatherData.sunset
    }
}
