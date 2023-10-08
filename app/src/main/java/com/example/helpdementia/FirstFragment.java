package com.example.helpdementia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;



public class FirstFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isMyLocationEnabled = false;


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
//
//        // Initialize the map
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);
//
//        return rootView;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in a specific location and move the camera
//        LatLng location = new LatLng(37.7749, -122.4194); // Example: San Francisco
//        mMap.addMarker(new MarkerOptions().position(location).title("Marker in San Francisco"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f));


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, container, false);

        // Request location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, initialize the map
            initMap();
        }
        // Initialize the Places API
        Places.initialize(requireContext(), "AIzaSyAaTrWQchBz-9D3Tn06x7DVrtkYmYz-1X0");

        // Create a PlacesClient instance
        PlacesClient placesClient = Places.createClient(requireContext());


        return rootView;
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, initialize the map
                initMap();
            } else {
                // Permission is denied, handle it (e.g., show a message to the user)
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Add a marker in a specific location and move the camera
        LatLng location = new LatLng(26.5832862, 87.9533753); // Example: San Francisco
        mMap.addMarker(new MarkerOptions().position(location).title("My Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 20.0f));

        enableMyLocation();
    }

    private void enableMyLocation() {
        if (!isMyLocationEnabled) {
            // Check if the location permission is granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, enable My Location
                mMap.setMyLocationEnabled(true);
               // mMap.setPadding(0, 1500, 0, 0);

                // Customize the My Location button and set a click listener
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        // Handle the "My Location" button click here
                        // You can implement any behavior you want

                        // Returning 'false' allows the default behavior (centering the map on the user's location)
                        // If you want to consume the event and prevent the default behavior, return 'true'.
                        return false; // Return 'true' to consume the event, 'false' to allow default behavior
                    }
                });

                isMyLocationEnabled = true;
            } else {
                // Request the location permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }
}

