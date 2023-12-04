package by.korsakovegor.taxiapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import by.korsakovegor.taxiapplication.databinding.ActivityTaxiBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class TaxiActivity : OnMapReadyCallback, FragmentActivity() {

    private lateinit var binding: ActivityTaxiBinding
    private lateinit var gMap: GoogleMap
    private lateinit var taxiLocation: LatLng
    private var isCreated = false
    private val activityScope = CoroutineScope(Dispatchers.Main)
    private var taxiList = listOf(
        LatLng(53.90263528726349, 27.556218489503017),
        LatLng(53.9169237550233, 27.56380554002881),
        LatLng(53.90435283060291, 27.56652712695937),
        LatLng(53.906923652657966, 27.52380039750065),
        LatLng(53.92230228080532, 27.601091283126085)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaxiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        activityScope.launch {
            delay(5000)
            changeVisibility()
            getTaxiLocation()
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.relocate.setOnClickListener {
            getTaxiLocation()
        }

        binding.confirmButton.setOnClickListener {
            val distance = intent.getDoubleExtra("distance", 0.0)
            val cost = (distance * 100).roundToInt() / 100.0

            val newIntent = Intent(this, CostActivity::class.java)
            newIntent.putExtra("cost", cost)
            newIntent.putExtra("origin", intent.getStringExtra("origin"))
            newIntent.putExtra("destination", intent.getStringExtra("destination"))
            startActivityForResult(newIntent, 100)
        }
    }

    private fun changeVisibility() {
        binding.loader.visibility = View.INVISIBLE
        binding.confirmButton.visibility = View.VISIBLE
        binding.relocate.visibility = View.VISIBLE
    }

    private fun getTaxiLocation() {
        if (!isCreated) {
            taxiLocation = taxiList[(taxiList.indices).random()]
            isCreated = true
        }
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taxiLocation, 15f))
        gMap.addMarker(
            MarkerOptions().position(taxiLocation).title("Taxi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(53.90070051658467, 27.559104022858435),
                12f
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100)
            onBackPressed()
    }
}