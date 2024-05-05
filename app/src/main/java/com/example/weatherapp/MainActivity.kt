package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import kotlin.properties.Delegates
import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.location.Location

import android.net.Uri
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.weatherapp.Models.WeatherResponse
import com.example.weatherapp.NetworkRequest.RetrofitObject
import com.example.weatherapp.NetworkRequest.WeatherService
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


class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mRefreshBtn:ImageView
        get() = findViewById(R.id.iv_refresh)
    private val TAG= MainActivity::class.java.simpleName



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

                WeatherDetails(this@MainActivity).getLocationWeatherDetails(latitude, longitude)

            }else{
                Log.i(TAG, "Latitude and Longitude null found!")
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

}