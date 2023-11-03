package com.example.helpdementia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;



public class FirstFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isMyLocationEnabled = false;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private MediaPlayer mediaPlayer;

    private boolean isBeepPlaying = false;
    private static final double FALL_THRESHOLD = 18; // You can adjust this value as per your needs

    private final long SMS_DELAY = 15000; // 20 seconds (in milliseconds)
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    private int smsSentCount = 0;
    private static final int MAX_SMS_COUNT = 2;

    private Handler handler = new Handler();

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private boolean beepCanceled = false;


    private Runnable sms_delay = () -> {
        // This code will be executed after the delay (20 seconds in this case)
        // Implement the event you want to trigger automatically here
        // For example, you can stop the beep sound or take any other action

        // Stop the beep sound if it's playing
        resetBeepSound();
    };


    @SuppressLint("InvalidWakeLockTag")
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




        // Inside your onCreateView method after initializing Places
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle the selected place (e.g., move the map to the selected location)
                LatLng selectedLocation = place.getLatLng();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15.0f));
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle any errors that occur during the search
            }
        });

        // Create a PlacesClient instance
        PlacesClient placesClient = Places.createClient(requireContext());


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update location every 10 seconds (adjust as needed)
        locationRequest.setFastestInterval(5000); // Fastest update interval is 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        // Initialize SensorManager
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize PowerManager and WakeLock
        powerManager = (PowerManager) requireActivity().getSystemService(Context.POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FallDetectionWakeLock");
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FallDetectionWakeLock");

        // Initialize the MediaPlayer to play the beep sound
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.adhuro);
        mediaPlayer.setLooping(true);


        return rootView;

    }






    @Override
     public void onResume() {
        super.onResume();
        // Register accelerometer sensor listener
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister accelerometer sensor listener to save resources
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Get the acceleration values
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            // Calculate the total acceleration (magnitude)
            double acceleration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

            // Check if the acceleration exceeds the fall threshold
            if (acceleration > FALL_THRESHOLD) {
                // Fall detected, handle the fall event here
                // For example, display a notification, call emergency services, etc.

                // Acquire WakeLock to keep the device awake during fall handling
                wakeLock.acquire();

                // Check if the acceleration exceeds the fall threshold
                if (acceleration > FALL_THRESHOLD) {
                    // Fall detected, handle the fall event here

                    // Play the beep sound if it's not already playing
                    if (!isBeepPlaying) {
                        playBeepSound();

                        // Show the dialog box here

                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                        builder.setTitle("Emergency Beep");
                        builder.setMessage("Cancel it in 10 secs. Otherwise, you will be redirected to the emergency Number.");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Release the WakeLock after handling the fall
                                wakeLock.release();
                                dialog.dismiss();
                                resetBeepSound();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        // Set the beepCanceled flag to false since the beep is playing
                        beepCanceled = false;

                        if (hasSmsPermission()) {
                            sendEmergencySMS();
                        } else {
                            // Request SMS permission
                            requestSmsPermission();
                        }
                    }

                    // Delayed action to check if the beep is canceled within 10 seconds
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Check if the beep is still playing after the delay
                            if (isBeepPlaying && !beepCanceled) {
                                // Beep was not canceled within 10 seconds, send emergency SMS
                                if (hasSmsPermission()) {
                                    sendEmergencySMS();
                                } else {
                                    // Request SMS permission
                                    requestSmsPermission();
                                }
                            }
                        }
                    }, 10000); // 10 seconds delay

                    // Acquire the wake lock
                    wakeLock.acquire();
                }
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }



    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                LOCATION_PERMISSION_REQUEST_CODE
        );

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // Add a marker in a specific location and move the camera
        LatLng location = new LatLng(26.5832862, 87.9533753); // Example: San Francisco
        mMap.addMarker(new MarkerOptions().position(location).title("My Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));

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




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }

    private void playBeepSound() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isBeepPlaying = true;
        }
    }

    private void resetBeepSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isBeepPlaying = false;
            beepCanceled = true;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        requestPermissions(new String[]{android.Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmergencySMS();
            } else {
                Toast.makeText(requireContext(), "SMS permission denied. Unable to send emergency SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            // Handle location updates here
            for (Location location : locationResult.getLocations()) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Process the location data as needed
            }
        }
    };

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }



    private void sendEmergencySMS() {
        // Check if the maximum number of SMS has been sent
        if (smsSentCount >= MAX_SMS_COUNT) {
            Toast.makeText(requireContext(), "Maximum number of emergency SMS already sent.", Toast.LENGTH_SHORT).show();
            return; // Do not send more SMS
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            // Request the current location
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Location obtained successfully
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Format the SMS message to include the live location
                        String phoneNumber = "9804931375"; // Replace with the emergency contact's phone number
                        String message = "FALL DETECTED! My current location is:\nLatitude: " + latitude + "\nLongitude: " + longitude;

                        // Send the SMS
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                            Toast.makeText(requireContext(), "Emergency SMS sent", Toast.LENGTH_SHORT).show();

                            // Increment the SMS sent count
                            smsSentCount++;
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "Failed to send emergency SMS", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        // Location is null (location retrieval failed)
                        Toast.makeText(requireContext(), "Failed to obtain current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    // ... Other existing code ...



}

