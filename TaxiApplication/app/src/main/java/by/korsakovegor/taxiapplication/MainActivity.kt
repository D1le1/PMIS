package by.korsakovegor.taxiapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import by.korsakovegor.taxiapplication.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : OnMapReadyCallback, FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var marker: Marker
    private var markerInitialized = false
    private lateinit var gMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        binding.search.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val loc = binding.search.query.toString()
                val geo = Geocoder(this@MainActivity, Locale.getDefault())
                    var geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        var addressList = geocoder.getFromLocationName(loc, 1)
                        if (addressList!!.size > 0) {
                            val latLng = LatLng(addressList[0].latitude, addressList[0].longitude)
                            try {
                                if (marker != null) {
                                    marker.remove()
                                }
                            } catch (_: Exception) {
                            }
                            var markerOptions =
                                MarkerOptions().position(latLng).title(addressList[0].featureName)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_AZURE
                                        )
                                    )
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            marker = gMap.addMarker(markerOptions)!!
                            binding.confirmButton.visibility = View.VISIBLE
                            binding.relocate2.visibility = View.VISIBLE
                            binding.relocate2.setOnClickListener {
                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@MainActivity, "Location not found", Toast.LENGTH_SHORT)
                    }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.relocate.setOnClickListener {
            getLocation()
        }
        binding.confirmButton.setOnClickListener {
            intent = Intent(this, TaxiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
            return
        }
        var task = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null);
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                var mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        var mapBelarus = LatLng(currentLocation.latitude, currentLocation.longitude)
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(mapBelarus))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapBelarus, 15.0f))
        googleMap.addMarker(MarkerOptions().position(mapBelarus).title("Client"))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getLocation()
        }
    }
}