package com.project.evcharz.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.evcharz.Model.BookingModel;
import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BookStation extends AppCompatActivity {
    String SelectedTimeStart ,SelectedTimeEnd;
    String selectedTimeStartTimeFormat,selectedTimeEndTimeFormat;
    String selected_vehicle_type;
    PlaceModel selectedStation;
    double selected_vehicle_rate;
    double bike_unit = 1.348/4;
    double car_unit = 2.48/4;
    double auto_unit = 2.48/4;
    CheckBox bike,car,auto;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String isSlotAvailable = "";
    ArrayList<BookingModel> bookingList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_staion);

        selectedStation = (PlaceModel) getIntent().getSerializableExtra("StationModel");

        TextView start_time = findViewById(R.id.start_time);
        TextView end_time = findViewById(R.id.end_time);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("booking_details");

        TextView station_name = findViewById(R.id.station_name_booking);
        TextView timing = findViewById(R.id.timing_booking_page);

        TextView instruction = findViewById(R.id.instruction);
        TextView current_date_time = this.findViewById(R.id.current_date_time);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formatted = df.format(new Date());

        current_date_time.setText(formatted);

        //set value
        station_name.setText(selectedStation.getPlace_name());
        timing.setText( "Rs "+selectedStation.getUnit_rate()+" Per Unit");


        Button btn_payment = findViewById(R.id.save_info);

         bike = this.findViewById(R.id.bike_checkbox);
         car = this.findViewById(R.id.car_checkbox);
         auto = this.findViewById(R.id.auto_checkbox);


            bike.setOnClickListener(v->{
                bike.setChecked(true);
                car.setChecked(false);
                auto.setChecked(false);

            });
            car.setOnClickListener(v->{
                bike.setChecked(false);
                car.setChecked(true);
                auto.setChecked(false);

            });
            auto.setOnClickListener(v->{
                bike.setChecked(false);
                car.setChecked(false);
                auto.setChecked(true);
            });


        start_time.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                if (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute < currentMinute)) {
                    // Prevent selection of past time
                    Toast.makeText(getApplicationContext(), "Invalid Time. Please select a valid time.", Toast.LENGTH_LONG).show();
                } else {
                    String min = String.valueOf(selectedMinute);
                    if (min.length() == 1) {
                        min = "0" + min;
                    }
                    selectedTimeStartTimeFormat = selectedHour + ":" + min + ":" + "00";
                    if (selectedHour < 12) {
                        SelectedTimeStart = selectedHour + " : " + min + " AM";
                    } else {
                        if (selectedHour != 12) {
                            selectedHour = selectedHour - 12;
                        }
                        SelectedTimeStart = selectedHour + " : " + min + " PM";
                    }
                    start_time.setText(SelectedTimeStart);
                }
            }, currentHour, currentMinute, true);

            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();
        });


        end_time.setOnClickListener(v -> {
            Calendar currentTime = Calendar.getInstance();
            int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
            int currentMinute = currentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                if (selectedHour < currentHour || (selectedHour == currentHour && selectedMinute < currentMinute)) {
                    // Prevent selection of past time
                    Toast.makeText(getApplicationContext(), "Invalid Time. Please select a valid time.", Toast.LENGTH_LONG).show();
                } else {
                    String min = String.valueOf(selectedMinute);
                    if (min.length() == 1) {
                        min = "0" + min;
                    }

                    selectedHour = selectedHour + 1; // Increment by 1 hour
                    int adjustedMinute = selectedMinute + 15; // Add 15 minutes
                    if (adjustedMinute >= 60) {
                        adjustedMinute -= 60;
                        selectedHour += 1;
                    }

                    String adjustedMin = String.valueOf(adjustedMinute);
                    if (adjustedMin.length() == 1) {
                        adjustedMin = "0" + adjustedMin;
                    }

                    selectedTimeEndTimeFormat = selectedHour + ":" + adjustedMin + ":" + "00";

                    if (selectedHour >= 0 && selectedHour < 12) {
                        SelectedTimeEnd = selectedHour + " : " + adjustedMin + " AM";
                    } else {
                        if (selectedHour != 12) {
                            selectedHour = selectedHour - 12;
                        }
                        SelectedTimeEnd = selectedHour + " : " + adjustedMin + " PM";
                    }
                    end_time.setText(SelectedTimeEnd);
                }
            }, currentHour, currentMinute, true);

            mTimePicker.setTitle("Select End Time");
            mTimePicker.show();
        });


        btn_payment.setOnClickListener(v->{
            try {
                if (checkSlotAvailability()){
                    if ((SelectedTimeStart == null || SelectedTimeEnd == null)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Please fill all the details");
                        builder.setCancelable(false);
                        builder.setNegativeButton("Retry", (dialog, which) -> dialog.cancel());
                        AlertDialog alert = builder.create();
                        alert.show();
                    }else{
                        double time_period = checkDuration();
                        Log.d("time_period", String.valueOf(time_period));

                        if (time_period % 15 == 0){
                            double  price;
                            try {
                                price = checkPrice();
                                if (price > 0){
                                    Intent i = new Intent(this,PaymentActivity.class);
                                    i.putExtra("price",new DecimalFormat("##.##").format(price));
                                    i.putExtra("StationModel",selectedStation);
                                    i.putExtra("start_time",selectedTimeStartTimeFormat);
                                    i.putExtra("end_time",selectedTimeEndTimeFormat);
                                    i.putExtra("vehicle_type",selected_vehicle_type);
                                    i.putExtra("unit_con",String.valueOf(selected_vehicle_rate));
                                    i.putExtra("duration",new DecimalFormat("##").format(time_period));
                                    startActivity(i);
                                }else{
                                    instruction.setText("error is in timeSlot");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            instruction.setText("time slot is not in multiple of 15 minutes");
                        }
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Slot Is Already Booked");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Change Time", (dialog, which) -> dialog.cancel());
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.backBtn_booking).setOnClickListener(v-> finish());
    }

    private double checkPrice() throws ParseException {
        double time_period = checkDuration();

        if(bike.isChecked()){
            selected_vehicle_rate = (bike_unit*(time_period/15));
            selected_vehicle_type = "bike";
        }else if(car.isChecked()){
            selected_vehicle_rate = (car_unit*(time_period/15));
            selected_vehicle_type = "car";
        }else if(auto.isChecked()){
            selected_vehicle_rate = (auto_unit*(time_period/15));
            selected_vehicle_type = "auto";
        }

        Log.d("unit_rate",selectedStation.getUnit_rate());
        double unit_rate = Double.parseDouble(selectedStation.getUnit_rate());
        return selected_vehicle_rate*unit_rate;
    }

    private long checkDuration() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date startDate = simpleDateFormat.parse(selectedTimeStartTimeFormat);
        Date endDate = simpleDateFormat.parse(selectedTimeEndTimeFormat);
        if(startDate != null && endDate != null){
            long difference = endDate.getTime() - startDate.getTime();
            if (difference < 0) {
                difference = (24 * 60 * 60 * 1000) - startDate.getTime() + endDate.getTime();
            }
            return difference / (1000 * 60);
        }
        return 0;
    }


    private boolean checkSlotAvailability(){
        bookingList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    bookingList.clear();
                    isSlotAvailable = "false";
                    BookingModel i = postSnapshot.getValue(BookingModel.class);
                    assert i != null;
                    if (Objects.equals(i.getStation_id(), selectedStation.getStation_id())) {
                        bookingList.add(i);
                    }
                }
                if (bookingList.isEmpty()) {
                    isSlotAvailable = "true";

                } else {
                    bookingList.forEach(item -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                            Date startTime = sdf.parse(item.getStart_time());
                            Calendar calendar1 = Calendar.getInstance();
                            assert startTime != null;
                            calendar1.setTime(startTime);

                            Date endTime = sdf.parse(item.getEnd_time());
                            Calendar calendar2 = Calendar.getInstance();
                            assert endTime != null;
                            calendar2.setTime(endTime);

                            Date randomTime = sdf.parse(SelectedTimeStart);
                            Calendar calendar3 = Calendar.getInstance();
                            assert randomTime != null;
                            calendar3.setTime(randomTime);

                            Date x = calendar3.getTime();

                            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                                isSlotAvailable = "false";
                            } else {
                                isSlotAvailable = "true";
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
        return Boolean.parseBoolean(isSlotAvailable);
    }

}