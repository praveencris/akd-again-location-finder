package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
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
    private var map: GoogleMap? = null
    private var locationInfo: LocationInfo? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        requestLocationPermission()


//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location

        return binding.root
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
         locationInfo?.apply {
             _viewModel.apply {
                 latitude.value=lat
                 longitude.value=lng
                 reminderSelectedLocationStr.value=selectedLocationString
                 selectedPOI.value=poi
             }
     }

    }

    override fun onPause() {
        super.onPause()
        onLocationSelected()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap

        if (isPermissionGranted()) {
            enableMyLocation()
            fusedLocationClient.lastLocation
                .addOnSuccessListener(requireActivity(), OnSuccessListener<Location?> { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        // Add a marker to Current Location and move the camera
                        locationInfo = LocationInfo(
                            lat = location.latitude,
                            lng = location.longitude,
                            selectedLocationString = "CUL",
                            poi = null
                        )

                        val lat = location.latitude
                        val lng = location.longitude
                        val currentLocation = LatLng(lat, lng)
                        val zoomLevel = 18f

                        /*
                        The zoom level controls how zoomed in you are on the map. The following list gives you an idea of what level of detail each level of zoom shows:

                        1: World
                        5: Landmass/continent
                        10: City
                        15: Streets
                        20: Buildings*/


                        // map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
                                zoomLevel
                            )
                        )
                        map?.addMarker(MarkerOptions().position(currentLocation))

                        /*
                            val overlaySize = 100f
                            val groundOverlay = GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
                                .position(homeLatLng, overlaySize)
                            map.addGroundOverlay(groundOverlay)


                            setMapLongClick(map)
                            setPoiClick(map)
                            setMapStyle(map)*/

                        setMapLongClick(map)
                        setPoiClick(map)
                        setMapStyle(map)

                    }
                })
        }

    }

    private fun setMapStyle(map: GoogleMap?) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style
                )
            )

            if (success == false) {
                Log.e(TAG, "Parsing to map style failed")
            }

        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setPoiClick(map: GoogleMap?) {
        map?.setOnPoiClickListener { poi ->
            map.clear()
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            locationInfo = LocationInfo(
                lat = poi.latLng.latitude,
                lng = poi.latLng.longitude,
                selectedLocationString = poi.name,
                poi = poi
            )
            //call showInfoWindow() on poiMarker to immediately show the info window.
            poiMarker.showInfoWindow()
        }
    }

    private fun setMapLongClick(map: GoogleMap?) {
        map?.setOnMapLongClickListener { latLng ->

            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                "Lat: %1$.5f, Long: %2.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.clear()
            map.addMarker(
                MarkerOptions().position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
            locationInfo = LocationInfo(
                lat = latLng.latitude,
                lng = latLng.longitude,
                selectedLocationString = getString(R.string.dropped_pin),
                poi = null
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map?.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            enableMyLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Required Permission, to enable location!",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        when {
            isPermissionGranted() -> {
                // You can use the API that requires the permission.
                enableMyLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
                Toast.makeText(
                    requireContext(),
                    "Permission needed to show current location on map!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    companion object {
        const val TAG = "SelectLocationFragment"
    }
}
