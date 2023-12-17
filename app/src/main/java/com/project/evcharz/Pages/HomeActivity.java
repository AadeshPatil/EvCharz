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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.evcharz.MainActivity;
import com.project.evcharz.Model.HostModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.Model.UserModel;
import com.project.evcharz.R;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, Serializable, LocationListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SearchView search_box;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<PlaceModel> stationList;
    GoogleMap googleMap1;
    CardView station_details;
    ArrayList<Marker> markerList;
    BitmapDescriptor station_icon;
    String loggedUserMbNumber;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        SharedPreferences sh = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        loggedUserMbNumber = sh.getString("loggedUserMbNumber", "");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chargingStationDetails");
        getLoggedInUserDetails();
        getAllChargingStation();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        station_details = findViewById(R.id.station_details);
        station_details.setVisibility(View.GONE);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        search_box = findViewById(R.id.idSearchView);
        search_box.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = search_box.getQuery().toString().trim();
                if (!location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(HomeActivity.this);
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(location, 1);
                        if (addressList != null && !addressList.isEmpty()) {
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                            if (googleMap1 != null) {
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(location);
                                googleMap1.addMarker(markerOptions);
                                googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                        } else {
                            showToast("Location not found");
                        }
                    } catch (IOException e) {
                        showToast("Error: Unable to find location");
                    }
                } else {
                    showToast("Please enter a location");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void getLoggedInUserDetails() {
        DatabaseReference userRef = firebaseDatabase.getReference("userDetails");
        String currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userRef.child(currentUid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                UserModel userModel = task.getResult().getValue(UserModel.class);
                if (userModel == null) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage("Please Complete the User Profile");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Update",
                            (dialog, which) -> {
                                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
                                startActivity(intent);
                            });
                    alertDialog.show();
                }
            }
        });
    }

    private void getAllChargingStation() {
        stationList = new ArrayList<>();
        markerList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stationList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PlaceModel i = postSnapshot.getValue(PlaceModel.class);
                    stationList.add(i);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    markerList = stationList.stream()
                            .map(station -> {
                                LatLng location = new LatLng(station.getLatitude(), station.getLongitude());
                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.charging_station_icon);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 100, false);
                                 station_icon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(location)
                                        .icon(station_icon)
                                        .snippet(String.valueOf(stationList.indexOf(station)));
                                Marker marker = googleMap1.addMarker(markerOptions);
                                if (marker != null) {
                                    marker.showInfoWindow();
                                }
                                return marker;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(ArrayList::new));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
            case R.id.nav_entertainment:
                Intent intent4= new Intent(HomeActivity.this, EntertainmentActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_Inquiry:
                Intent intent5 = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(intent5);
                break;
            case R.id.nav_host_view:
                DatabaseReference hostRef = firebaseDatabase.getReference("hostUserList");
                hostRef.child(loggedUserMbNumber).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Intent intent6 = new Intent(HomeActivity.this, HostRegisterActivity.class);
                        startActivity(intent6);
                    } else {
                        HostModel hostModel = task.getResult().getValue(HostModel.class);
                        Intent intent6;
                        if (hostModel == null) {
                            intent6 = new Intent(HomeActivity.this, HostRegisterActivity.class);
                        } else {
                            intent6 = new Intent(HomeActivity.this, HostViewActivity.class);
                        }
                        startActivity(intent6);
                    }
                });
                break;
            case R.id.nav_Logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Logout!");
                builder.setMessage("Are you sure you want to logout ?");
                builder.setPositiveButton("YES", (dialog, which) -> {
                    SharedPreferences.Editor editor = getSharedPreferences("LoginDetails", MODE_PRIVATE).edit();
                    editor.putString("loggedUserMbNumber", "");
                    editor.apply();
                    Intent i = new Intent(HomeActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap1 = googleMap;
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        } else {
            googleMap1.setMyLocationEnabled(true);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            View locationButton = ((View) findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            }
        }
    }
    private void markCurrentLocation(LatLng currentLocation,GoogleMap googleMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.bike_marker_ico);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
        BitmapDescriptor bike_marker = BitmapDescriptorFactory.fromBitmap(smallMarker);
        markerOptions.icon(bike_marker);
        markerOptions.title("Current Location");
        googleMap.addMarker(markerOptions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateStationsDetails(googleMap,currentLocation.latitude,currentLocation.longitude);
        }
        googleMap.setOnMapClickListener(v-> station_details.setVisibility(View.GONE));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    private void updateStationsDetails(GoogleMap googleMap, double latitude, double longitude) {
        googleMap.setOnMarkerClickListener(m -> {
            if (m.getSnippet() != null) {
                if (markerList != null) {
                    markerList.forEach(marker -> {
                        if (!marker.equals(m)) {
                            marker.setIcon(station_icon);
                        }
                    });
                }
                Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.click_charging_station);
                Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 80, 100, false);
                BitmapDescriptor clicked_station = BitmapDescriptorFactory.fromBitmap(smallMarker2);
                m.setIcon(clicked_station);
                station_details.setVisibility(View.VISIBLE);
                PlaceModel station = stationList.get(Integer.parseInt(m.getSnippet()));
                TextView placeName = findViewById(R.id.station_name_booking);
                TextView rate = findViewById(R.id.timing_booking_page);
                Button bookSlot = findViewById(R.id.reserve_btn);

                if (placeName != null && rate != null && bookSlot != null) {
                    placeName.setText(station.getPlace_name());
                    rate.setText("₹ " + station.getUnit_rate() + " per unit");
                    bookSlot.setOnClickListener(v -> {
                        Intent i = new Intent(HomeActivity.this, ViewDetails.class);
                        i.putExtra("StationModel", station);
                        i.putExtra("cur_Latitude", String.valueOf(latitude));
                        i.putExtra("cur_Longitude", String.valueOf(longitude));
                        startActivity(i);
                    });
                }
            }
            return false;
        });
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markCurrentLocation(latLng,googleMap1);
        googleMap1.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }
}
