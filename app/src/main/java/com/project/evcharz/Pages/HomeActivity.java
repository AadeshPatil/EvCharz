package com.project.evcharz.Pages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.evcharz.MainActivity;
import com.project.evcharz.Model.HostModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, Serializable {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;
    SupportMapFragment supportMapFragment;

    SearchView search_box;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<PlaceModel> stationList;
    GoogleMap googleMap1;
    Marker marker1;
    CardView station_details;
    ArrayList<Marker> markerList;
    BitmapDescriptor station_icon;
    String loggedUserMbNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        loggedUserMbNo = sh.getString("loggedUserMbNo", "");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chargingStationDetails");
        getAllChargingStation();
        // Menu Drawer

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        textView = findViewById(R.id.textView);
        toolbar = findViewById(R.id.toolbar);


        station_details = findViewById(R.id.station_details);
        station_details.setVisibility(View.GONE);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        search_box = this.findViewById(R.id.idSearchView);

//        google map
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gMap);


        search_box.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search_box.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    googleMap1.addMarker(new MarkerOptions().position(latLng).title(location));
                    googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        supportMapFragment.getMapAsync(this);


    }


    private void getAllChargingStation() {
        stationList = new ArrayList<>();
        markerList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                stationList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PlaceModel i = postSnapshot.getValue(PlaceModel.class);
                    stationList.add(i);
                }
                for (int i=0;i<= stationList.size()-1;i++){
                    PlaceModel j = stationList.get(i);
                    double lat = j.getLatitude();
                    double longi = j.getLongitude();
                    LatLng location1 = new LatLng(lat, longi);
                    Log.d("hello", String.valueOf(location1));
                    int height = 100;
                    int width = 80;
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.charging_station_icon);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    station_icon = BitmapDescriptorFactory.fromBitmap(smallMarker);

                    marker1=googleMap1.addMarker(
                            new MarkerOptions()
                                    .position(location1)
                                    .snippet(""+i)
                                    .icon(station_icon)
                    );
                    assert marker1 != null;
                    marker1.showInfoWindow();
                    markerList.add(marker1);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_profile:
                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_booking:
                Intent intent2 = new Intent(HomeActivity.this, MyBookingActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_wallet:
                Intent intent3 = new Intent(HomeActivity.this, WalletActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_About_us:
                Intent intent4 = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_host_view:
                DatabaseReference hostRef = firebaseDatabase.getReference("hostUserList");
                hostRef.child(loggedUserMbNo).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Intent intent5 = new Intent(HomeActivity.this, HostRegisterActivity.class);
                        startActivity(intent5);
                    }
                    else {
                        HostModel hostModel = task.getResult().getValue(HostModel.class);
                        Intent intent5;
                        if (hostModel == null){
                            intent5 = new Intent(HomeActivity.this, HostRegisterActivity.class);
                        }else{
                            intent5 = new Intent(HomeActivity.this, HostViewActivity.class);
                        }
                        startActivity(intent5);
                    }
                });
                break;
            case R.id.nav_Logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Logout!");
                builder.setMessage("Are you sure you want to logout ?");

                builder.setPositiveButton("YES", (dialog, which) -> {
                    SharedPreferences.Editor editor = getSharedPreferences("LoginDetails", MODE_PRIVATE).edit();
                    editor.putString("loggedUserMbNo", "");
                    editor.apply();
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    dialog.dismiss();
                });

                builder.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
                AlertDialog alert = builder.create();
                alert.show();


                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
            googleMap1=googleMap;

            Criteria criteria = new Criteria();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                googleMap.setMinZoomPreference(10.0f);

                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                double latitude = 0;
                double longitude = 0;

                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }
                else{
                    locationManager.requestLocationUpdates(provider, 1000, 0, (LocationListener) this);
                }

                LatLng currentLocation = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(currentLocation);

                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bike_marker_ico);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                BitmapDescriptor bike_marker = BitmapDescriptorFactory.fromBitmap(smallMarker);

                markerOptions.icon(bike_marker);
                markerOptions.title("Current Location");
                googleMap.addMarker(markerOptions);
//
                googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));

                View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                rlp.setMargins(0, 0, 30, 30);


                double finalLatitude1 = latitude;
                double finalLongitude1 = longitude;
                googleMap.setOnMarkerClickListener(m ->{

//                    Log.d("Marker", String.valueOf(m.getSnippet()));
                    markerList.forEach((n) -> n.setIcon(station_icon));

                    if (m.getSnippet() != null) {

                        Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.click_charging_station);
                        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 80, 100, false);
                        BitmapDescriptor clicked_station = BitmapDescriptorFactory.fromBitmap(smallMarker2);

                        m.setIcon(clicked_station);

                        station_details.setVisibility(View.VISIBLE);
                        PlaceModel Station = stationList.get(Integer.parseInt(m.getSnippet()));
                        Log.d("Marker", String.valueOf(Station.getPlace_name()));


                        TextView Place_name = findViewById(R.id.station_name_booking);
                        TextView rate = findViewById(R.id.timing_booking_page);
                        Button book_slot = findViewById(R.id.reserve_btn);

                        Place_name.setText(Station.getPlace_name());
                        rate.setText("₹ "+Station.getUnit_rate() +" per unit");

                        book_slot.setOnClickListener(v->{

                            Intent i;
                            i = new Intent(this, ViewDetails.class);
                            i.putExtra("StationModel",Station);
                            i.putExtra("cur_Latitude",String.valueOf(finalLatitude1));
                            i.putExtra("cur_Longitude",String.valueOf(finalLongitude1));
                            startActivity(i);
                        });
                    }
                    return false;
                });
            }




            googleMap.setOnMapClickListener(v->{
                station_details.setVisibility(View.GONE);
            });

    }

    }
