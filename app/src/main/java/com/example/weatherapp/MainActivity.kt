package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.location.Location

import android.net.Uri
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Models.WeatherResponse
import com.example.weatherapp.NetworkRequest.RetrofitObject
import com.example.weatherapp.NetworkRequest.WeatherService
import com.example.weatherapp.viewModel.WeatherViewModel
import com.example.weatherapp.viewModel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mRefreshBtn:ImageView
        get() = findViewById(R.id.iv_refresh)

    private val progressLayout: LinearLayout
        get() = findViewById(R.id.loading_layout)

    private val mEmptyTextView: TextView
        get() = findViewById(R.id.empty_tv)

    private val WeatherLayout: LinearLayout
        get() = findViewById(R.id.weather_layout)
    private val mTvMain: TextView
        get() = findViewById(R.id.tv_main)

    private val mTvDescr: TextView
        get() = findViewById(R.id.tv_main_description)

    private val mTvTemp: TextView
        get() = findViewById(R.id.tv_temp)

    private val mTvHumidity: TextView
        get() = findViewById(R.id.tv_humidity)

    private val mTvMin: TextView
        get() = findViewById(R.id.tv_min)

    private val mTvMax: TextView
        get() = findViewById(R.id.tv_max)

    private val mTvSpeed: TextView
        get() = findViewById(R.id.tv_speed)

    private val mTvSpeedUnit: TextView
        get() = findViewById(R.id.tv_speed_unit)

    private val mTvCountry: TextView
        get() = findViewById(R.id.tv_country)

    private val mTvName: TextView
        get() =findViewById(R.id.tv_name)

    private val mTvSunrise: TextView
        get() = findViewById(R.id.tv_sunrise_time)

    private val mTvSunset: TextView
        get() = findViewById(R.id.tv_sunset_time)

    private val mIvMain:ImageView
        get() = findViewById(R.id.iv_main)

    //private val TAG= MainActivity::class.java.simpleName
    private val TAG="MainActivity"

    private lateinit var mViewModel:WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this)

        if(!isLocationEnable()){
            Toast.makeText(this, "Your location provider is turned off. Please turn it on.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }else{
            requestLocationPermission();

        }
        mRefreshBtn.setOnClickListener {
            requestLocationData()
        }

    }
    private fun requestLocationPermission(){
        // request the permission in current context
        Dexter.withContext(this)
            // below line is use to request the number of permissions which are required in our app.
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            // after adding permissions we are calling a listener method.
            .withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // this method is called when all permissions are granted
                    if(report!!.areAllPermissionsGranted()){
                        Toast.makeText(this@MainActivity, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        requestLocationData()
                    }
                    // check for permanent denial of any permission
                    if(report.isAnyPermissionPermanentlyDenied()){
                        Toast.makeText( this@MainActivity,
                            "You have denied location permission.Please enable them as it is mandatory for the app to work.",
                            Toast.LENGTH_SHORT).show()
                        // you can consider this : token?.continuePermissionRequest()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // this method is called when user grants some permission and denies some of them.
                    showRationalDialogForPermissions()


                }
            })
            //If you want to receive permission listener callbacks on the same thread that fired the permission request, you just need to call onSameThread before checking for permissions
            .onSameThread().check()


    }

    /*
    * @SuppressLint: This is an annotation provided by the Android framework specifically for suppressing lint warnings.
    * lint is a tool that scan code and identify the issue or any bugs.
    * "MissingPermission": This is the specific lint check that you're suppressing. In this case, it's warning about missing permissions. This lint check looks for cases where certain sensitive operations,
       such as accessing the user's location, are performed without the necessary permissions being declared in the manifest file or requested at runtime.
    * you're essentially telling the lint tool to ignore any warnings it might generate for this method related to missing permissions. This is commonly used when you're sure that the necessary permissions
      are already being handled properly elsewhere in the code or when you have some special handling for permissions that the lint tool doesn't recognize.
    */
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        //This line sets the priority of the location request to high accuracy.
        mLocationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }
    private val mLocationCallback=object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation : Location? =locationResult.lastLocation
            val latitude=mLastLocation?.latitude
            val longitude=mLastLocation?.longitude
            Log.i("Longitude: ", longitude.toString())
            Log.i("Latitiue: ", latitude.toString())
            if (latitude != null && longitude != null) {
                Log.i(TAG, "enter into weather Detail")

                //WeatherDetails(this@MainActivity).getLocationWeatherDetails(latitude, longitude)
                val repository=(application as WeatherApplication).weatherRepository
                mViewModel=ViewModelProvider(this@MainActivity, WeatherViewModelFactory(repository, latitude, longitude))[WeatherViewModel::class.java]
                mViewModel.weatherResponse.observe(this@MainActivity){
                    progressLayout.visibility= View.GONE
                    if(it!=null){
                        //Log.i("KanhaiyaResponse", "Data available!")
                        WeatherLayout.visibility=View.VISIBLE
                        setUI(it)
                    }else{
                        //Log.i("KanhaiyaResponse", "Data not available!")
                        mEmptyTextView.visibility=View.VISIBLE
                        mEmptyTextView.text="Data not available!"
                    }
                }

            }else{
                Log.i(TAG, "Latitude and Longitude null found!")
                progressLayout.visibility= View.GONE
                mEmptyTextView.visibility=View.VISIBLE
                mEmptyTextView.text="Location is not accessiable!"
            }
        }
    }

    // below is the shoe setting dialog method which is use to display a dialogue message.
    private fun showRationalDialogForPermissions(){
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder(this)
            .setTitle("Need Permissions")
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under App Settings.")
            .setPositiveButton(
                "GO TO SETTINGS",
            ){_,_ ->
                // this method is a onClickListener which is called on click on positive button and on clicking shit button
                // we are redirecting our user from our app to the settings page of our app.
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                // this method is called when user click on negative button.
                dialog.dismiss()
            }.show()
    }

    private fun isLocationEnable():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun setUI(weatherList:WeatherResponse) {
        val weather=weatherList.weather[0]
        mTvMain.text=weather.main
        mTvDescr.text=weather.description
        val main=weatherList.main
        //context.application.resources.configuration.locales.toString() :- here we are accessing the configuration of app using resource and then locales which will give location code.
        mTvTemp.text=getString(R.string.format_detail, main.temp.toString(), getUnit(application.resources.configuration.locales[0].toString()))
        //Log.i("Locals", context.application.resources.configuration.locales[0].toString())
        mTvHumidity.text=getString(R.string.format_detail, main.humidity.toString(), "%")
        mTvMin.text=getString(R.string.format_detail,main.temp_min.toString(), "min")
        mTvMax.text=getString(R.string.format_detail,main.temp_min.toString(), "min")
        mTvSunrise.text=formatTime(weatherList.sys.sunrise)
        mTvSunset.text=formatTime(weatherList.sys.sunset)

        mTvSpeed.text=weatherList.wind.speed.toString()

        mTvName.text=weatherList.name
        mTvCountry.text=weatherList.sys.country

        mIvMain.setImageResource(
            when(weather.icon){
                "01d"->R.drawable.sunny
                "02d", "03d", "04d", "04n", "01n", "02n","03n", "10n"->R.drawable.cloud
                "10d", "11n"->R.drawable.rain
                "11d"->R.drawable.storm
                "13d","13n"->R.drawable.snowflake
                else ->R.drawable.sunny

            }
        )
    }

    private fun getUnit(value:String):String{
        var result:String?=null
        result=when(value) {
            //US: United States , LR: Liberia , MM: Myanmar
            "US", "LR", "MM" -> "℉"
            else -> "℃"
        }
        return result
    }
    private fun formatTime(time:Long):String{
        // the time is in second , to convert it in millisecond we have multiple it by 1000
        val date= Date(time*1000L)
        val format= SimpleDateFormat("HH:mm", Locale("en", "IN"))
        format.timeZone= TimeZone.getDefault()
        return format.format(date)
    }

}