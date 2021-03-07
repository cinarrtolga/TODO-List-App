package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {
    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var navController: NavController
    var selectedLat = 51.60430713353941
    var selectedLong = -0.06614643925656856
    var locationName = "White Hart Lane"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val map = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        map.getMapAsync(this)

        navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.popBackStack(R.id.saveReminderFragment, false)
        }

        binding.saveLocationButton.setOnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        /*Default location information. If app can not get current location information*/
        val latitude = 51.60430713353941
        val longitude = -0.06614643925656856
        val defaultLocation = LatLng(latitude, longitude)
        val zoomLevel = 15f

        /*Here is a permission check for the set location. If permission is not granted, map initialized with default starting location.*/
        if(isLocationEnabled()) {
            val locationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            val lastLocation = locationProviderClient.lastLocation
            lastLocation.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Set the map's camera position to the current location of the device.
                    val lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation.latitude,
                                        lastKnownLocation.longitude), 15f))
                    }
                } else {
                    map.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, 15f))
                    map.uiSettings?.isMyLocationButtonEnabled = false
                }
            }

        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel))
        }

        onLongTouch(googleMap)
        onTouch(googleMap)
        setMapStyle(googleMap)
    }

    /*Save button function. Returns to save reminder fragments with variables.*/
    private fun onLocationSelected() {
        _viewModel.latitude.value = selectedLat
        _viewModel.longitude.value = selectedLong
        _viewModel.reminderSelectedLocationStr.value = locationName
        navController.popBackStack()
    }

    private fun onLongTouch(map: GoogleMap){
        map.setOnPoiClickListener { poi ->
            /*If the user changes the decision, this line clears the map.*/
            map.clear()

            selectedLat = poi.latLng.latitude
            selectedLong = poi.latLng.longitude
            locationName = poi.name

            map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
        }
    }

    private fun onTouch(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            /*If the user changes the decision, this line clears the map.*/
            map.clear()

            selectedLat = latLng.latitude
            selectedLong = latLng.longitude
            locationName = "Custom location"

            map.addMarker(
                    MarkerOptions()
                            .position(latLng)
                            .title("Custom location")
            )
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            R.raw.map_style
                    )
            )

            if(!success) {
                Log.i("Information", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.i("Information", "Can't find style. Error", e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun isLocationEnabled(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else {
            googleMap.isMyLocationEnabled = true
            true
        }
    }
}
