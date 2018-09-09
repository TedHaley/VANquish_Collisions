package com.projects.valerian.samplemapapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.projects.valerian.samplemapapplication.model.Suggestion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MapsActivity : AppCompatActivity() {

    private lateinit var locationMarker: Marker
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var map: GoogleMap? = null

    private var hasLocationPermission = false
    private var suggestionMarkers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.content) as SupportMapFragment

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapFragment.getMapAsync {
            map = it
            setupMap(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SEND_SUGGESTION ->
                if (resultCode == Activity.RESULT_OK) showSnackbar(main,"Thanks for your suggestion!")
            else ->
                super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) =
            when (requestCode) {
                REQUEST_LOCATION_PERMISSION -> {
                    hasLocationPermission = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (hasLocationPermission) {
                        map?.let { setupMap(it) }
                        Unit
                    } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }

    private fun setupMap(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
        } else {
            hasLocationPermission = true
            googleMap.run {
                isMyLocationEnabled = true

                setOnInfoWindowClickListener {
                    if (it.tag is Suggestion) {
                        launchDetailActivity(this@MapsActivity, it.tag as Suggestion)
                    } else {
                        launchAddSuggestionActivity(this@MapsActivity, it.position)
                    }
                }

                setOnMapLongClickListener { latLng ->
                    locationMarker.run {
                        position = latLng
                        snippet = "%.4f, %.4f".format(position.longitude, position.latitude)
                    }
                }

                loadSuggestions(googleMap)

                loadUserLocation(googleMap)
            }
        }
    }

    private fun loadSuggestions(googleMap: GoogleMap) {
        getApiInstance().getSuggestions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    response.forEach {
                        val marker = googleMap.addMarker(MarkerOptions().suggestion(it)).apply {
                            tag = it
                        }
                        suggestionMarkers.add(marker)
                    }
                }, { println(it.localizedMessage) })
    }

    private fun loadUserLocation(googleMap: GoogleMap) = googleMap.run {
        if (ContextCompat.checkSelfPermission(this@MapsActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // load current location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                moveCamera(CameraUpdateFactory.zoomTo(14.0f))
                location?.let {
                    val coords = LatLng(it.latitude, it.longitude)
                    locationMarker = addMarker(MarkerOptions().default(coords)).apply {
                        showInfoWindow()
                    }
                    moveCamera(CameraUpdateFactory.newLatLng(coords))
                } ?: showSnackbar(main, "Unable to get last known location")
            }
        }
    }

    private fun MarkerOptions.default(latLng: LatLng) = MarkerOptions()
            .position(latLng)
            .title("Click to suggest safety improvement")
            .snippet("%.4f, %.4f".format(latLng.latitude, latLng.longitude))

    private fun MarkerOptions.suggestion(suggestion: Suggestion) = suggestion.run {
        val latLng = LatLng(lat, lon)
        MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .title("Suggestion")
                .snippet("More info...")
    }

    private fun List<Marker>.clear() = this.forEach { it.remove() }

    private fun launchAddSuggestionActivity(context: Context, latLng: LatLng) {
        startActivityForResult(AddSuggestionActivity.createIntent(
                context,
                latLng.latitude,
                latLng.longitude),
                REQUEST_CODE_SEND_SUGGESTION)
    }

    private fun launchDetailActivity(context: Context, suggestion: Suggestion) {
        startActivity(DetailsActivity.createIntent(
                context,
                suggestion))
    }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 0
        const val REQUEST_CODE_SEND_SUGGESTION = 1
    }
}
