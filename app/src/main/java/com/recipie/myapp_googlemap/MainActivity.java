package com.recipie.myapp_googlemap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GoogleMap map;
    Marker marker ; // method 1

    Location lastLocation ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment fragment = new SupportMapFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_viewer, fragment, "map");
        transaction.commit();

        OnMapReadyCallback listener = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                final LatLng location = new LatLng(6.897221, 79.860329);

                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder();
                cameraBuilder.target(location);
                cameraBuilder.zoom(18);
                CameraPosition cameraPosition = cameraBuilder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.animateCamera(cameraUpdate);

                //Add Location Marker
                final MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.title("You");
//                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_point));

                // method 1
                if (MainActivity.this.marker != null){
                        MainActivity.this.marker.remove();
                }
                MainActivity.this.marker = map.addMarker(markerOptions);
                // method 1

                marker.showInfoWindow();
                //Add Location Marker

                // change map theme
                MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style);
                map.setMapStyle(styleOptions);
                // change map theme

                // set Marker listener
                GoogleMap.OnMarkerClickListener markerListener = new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Toast.makeText(getApplicationContext(), "Java Institute", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                };
                map.setOnMarkerClickListener(markerListener);
                // set Marker listener

                // enable my location
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                // enable my location

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(1000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                LocationCallback listener2 = new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) { // location 1 change una gaman ma result eka denne me method eken
                        lastLocation = locationResult.getLastLocation();

                        LatLng location2 = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

                        // add new Marker
                        MarkerOptions markerOptions2 = new MarkerOptions();
                        markerOptions2.position(location2);
                        markerOptions2.title("Bell");
                        //markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bell));
                        // markerOptions2.flat(false); // false : map 1 rotate weddi icon 1 rotate wenne na.....
                        // map.clear(); // sampurna map ekama clear karanawaa

                        MainActivity.this.marker.remove(); // method 1
                        MainActivity.this.marker = map.addMarker(markerOptions2); // method 1
                        // add new Marker
                    }
                };

                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                client.requestLocationUpdates(locationRequest,listener2,Looper.myLooper());


                UiSettings settings = map.getUiSettings();
                settings.setZoomControlsEnabled(true);
                settings.setMapToolbarEnabled(true);
                settings.setZoomGesturesEnabled(true);
                settings.setCompassEnabled(true);


                GoogleMap.OnMapLongClickListener listener3 = new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.title("Long Press");
                        options.draggable(true);
                        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hand_pointer));
                        Marker marker3 = map.addMarker(options);
                        marker3.showInfoWindow();

                        //draw line
                        PolylineOptions options4 = new PolylineOptions();
                        options4.add(latLng);
                        LatLng latLng2  = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                        options4.add(latLng2);
                        options4.color(getResources().getColor(R.color.colorAccent));
                        options4.width(10);
                        map.addPolyline(options4);
                        //draw line
                    }
                };
                map.setOnMapLongClickListener(listener3);

                // get nearby  locations
                try {
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    List<Address> list = geocoder.getFromLocation(6.897221, 79.860329,5);
                    final String names[] = new String[5];
                    int index = 0 ;
                        for (Address address : list){
                           names[index] = address.getAddressLine(0);
                           index ++ ;
                        }
                        DialogInterface.OnClickListener l1 = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this,names[i],Toast.LENGTH_SHORT).show();
                            }
                        };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    // builder.setIcon(R.drawable.ic_bell);
                    builder.setTitle("Nearby Locations");
                    builder.setSingleChoiceItems(names,5,l1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // get nearby locations
            }
        };

        fragment.getMapAsync(listener);
    }
}
