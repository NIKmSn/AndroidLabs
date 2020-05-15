package com.example.lab8

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

private const val PERMISSION_REQUEST = 1

class RouteActivity : FragmentActivity(), OnMapReadyCallback  {

    private lateinit var mMap: GoogleMap
    private lateinit var origin: LatLng
    private lateinit var orName: String
    private lateinit var destination: LatLng
    private lateinit var destName: String

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var currentLocationLatitude: Double = 0.0
    private var currentLocationLongitude: Double = 0.0

    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        if (checkPermission(permissions)) {
            getLocation()
        } else {
            requestPermissions(permissions, PERMISSION_REQUEST)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.routesMap) as SupportMapFragment
        origin = LatLng(intent.getDoubleExtra("originLat", 0.0),
            intent.getDoubleExtra("originLong", 0.0))
        orName = intent.getStringExtra("originName").toString()

        destination = LatLng(intent.getDoubleExtra("destinationLat", 0.0),
            intent.getDoubleExtra("destinationLong", 0.0))
        destName = intent.getStringExtra("destinationName").toString()

        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.addMarker(MarkerOptions().position(LatLng(currentLocationLatitude, currentLocationLongitude))
            .title("Current Position")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        mMap.addMarker(MarkerOptions().position(origin).title(orName))
        mMap.addMarker(MarkerOptions().position(destination).title(destName))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 15f))

        val URL = getDirectionURL(origin, destination)
        Log.d("GoogleMap", "URL : $URL")
        GetDirection(URL).execute()
    }

    fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude}," +
                "${origin.longitude}&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false&mode=driving&key=${getString(R.string.google_maps_key)}"
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.d("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,GMapsDTO::class.java)

                val path =  ArrayList<LatLng>()
                var testvar: String = ""

                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            if(result.size == 0) {
                Toast.makeText(this@RouteActivity,
                    "There are no any routes",
                    Toast.LENGTH_LONG).show()
            }
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLACK)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                Log.d("Current Location", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            currentLocationLatitude = locationGps!!.latitude
                            currentLocationLongitude = locationGps!!.longitude

                            Log.d("Current Location", " GPS Latitude : " + locationGps!!.latitude)
                            Log.d("Current Location", " GPS Longitude : " + locationGps!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                    override fun onProviderEnabled(provider: String?)  {}

                    override fun onProviderDisabled(provider: String?) {}

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("Current Location", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            currentLocationLatitude = locationNetwork!!.latitude
                            currentLocationLongitude = locationNetwork!!.longitude
                            Log.d("Current Location", " Network Latitude : " + locationNetwork!!.latitude)
                            Log.d("Current Location", " Network Longitude : " + locationNetwork!!.longitude)
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

                    override fun onProviderEnabled(provider: String?) {}

                    override fun onProviderDisabled(provider: String?) {}

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    currentLocationLatitude = locationNetwork!!.latitude
                    currentLocationLongitude = locationNetwork!!.longitude
                    Log.d("Current Location", " Network Latitude : " + locationNetwork!!.latitude)
                    Log.d("Current Location", " Network Longitude : " + locationNetwork!!.longitude)
                }else{
                    currentLocationLatitude = locationGps!!.latitude
                    currentLocationLongitude = locationGps!!.longitude
                    Log.d("Current Location", " GPS Latitude : " + locationGps!!.latitude)
                    Log.d("Current Location", " GPS Longitude : " + locationGps!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Grant permission in the settings", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
