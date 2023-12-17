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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BookStation extends AppCompatActivity {
    Calendar selectedTimeStartTimeFormat,selectedTimeEndTimeFormat;
    String selected_vehicle_type;
    PlaceModel selectedStation;
    double selected_vehicle_rate;
    double bike_unit = 1.348/4;
    double car_unit = 2.48/4;
    double auto_unit = 2.48/4;
    CheckBox bike,car,auto;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Boolean isSlotAvailable = true;
    ArrayList<BookingModel> bookingList;
    TextView start_time,end_time;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_staion);

        selectedStation = (PlaceModel) getIntent().getSerializableExtra("StationModel");

         start_time = findViewById(R.id.start_time);
         end_time = findViewById(R.id.end_time);

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

        start_time.setOnClickListener(v -> showTimePickerDialog(start_time));
        end_time.setOnClickListener(v -> showTimePickerDialog(end_time));


        btn_payment.setOnClickListener(v->{
            try {
                if (checkSlotAvailability()){
                    double time_period = checkDuration();
                    time_period = Math.ceil(time_period / 15) * 15;
                    if (time_period % 15 == 0){
                            double  price;
                            try {
                                price = checkPrice();
                                if (price > 0){
                                    Intent i = new Intent(this,PaymentActivity.class);
                                    i.putExtra("price",new DecimalFormat("##.##").format(price));
                                    i.putExtra("StationModel",selectedStation);
                                    i.putExtra("start_time", selectedTimeStartTimeFormat != null ? formatExactTime(selectedTimeStartTimeFormat) : "14:00:00");
                                    i.putExtra("end_time", selectedTimeEndTimeFormat != null ? formatExactTime(selectedTimeEndTimeFormat) : "15:00:00");
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


    public String formatExactTime(Calendar time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        return sdf.format(time.getTime());
    }
    private void showTimePickerDialog(TextView textView) {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

         TimePickerDialog mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
            selectedTime.set(Calendar.MINUTE, selectedMinute);

             if (textView.getId() == R.id.end_time) {
                 selectedTimeEndTimeFormat = selectedTime;
             } else if (textView.getId() == R.id.start_time){
                 selectedTimeStartTimeFormat = selectedTime;
             }

             if (selectedTime.before(currentTime)) {
                Toast.makeText(getApplicationContext(), "Invalid Time. Please select a valid time.", Toast.LENGTH_LONG).show();
            } else {
                String selectedTimeFormat = formatTime(selectedHour, selectedMinute);
                textView.setText(selectedTimeFormat);
            }
        }, currentHour, currentMinute, false);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    private String formatTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh : mm a", Locale.getDefault());
        return timeFormat.format(calendar.getTime());
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
        Date startDate = selectedTimeStartTimeFormat.getTime();
        Date endDate = selectedTimeEndTimeFormat.getTime();
        long difference = endDate.getTime() - startDate.getTime();
        if (difference < 0) {
            difference = (24 * 60 * 60 * 1000) - startDate.getTime() + endDate.getTime();
        }
        return difference / (1000 * 60);
    }


    private boolean checkSlotAvailability() {
        bookingList = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isSlotAvailable = true;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        BookingModel i = postSnapshot.getValue(BookingModel.class);
                        if (i != null && selectedStation != null && selectedStation.getStation_id() != null) {
                            if (Objects.equals(i.getStation_id(), selectedStation.getStation_id())) {
                                bookingList.add(i);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getMessage()));
                    }
                }

                if (!bookingList.isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

                    LocalTime startTime = LocalTime.parse(formatExactTime(selectedTimeStartTimeFormat),formatter);
                    LocalTime endTime = LocalTime.parse(formatExactTime(selectedTimeEndTimeFormat),formatter);

                    for (BookingModel item : bookingList) {
                        if(item.getEnd_time() != null && item.getStart_time() != null){
                            LocalTime itemStartTime = LocalTime.parse(item.getStart_time(),formatter);
                            LocalTime itemEndTime = LocalTime.parse(item.getEnd_time(),formatter);

                            if ((startTime.isAfter(itemStartTime) && startTime.isBefore(itemEndTime)) ||
                                    (endTime.isAfter(itemStartTime) && endTime.isBefore(itemEndTime)) ||
                                    startTime.equals(itemStartTime) || endTime.equals(itemEndTime)) {
                                isSlotAvailable = false;
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("The read failed: " , databaseError.getMessage());
            }
        });
        return isSlotAvailable;
    }

}