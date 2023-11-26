package com.project.wheels_on_demand.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.wheels_on_demand.Model.PlaceModel;
import com.project.wheels_on_demand.R;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookStation extends AppCompatActivity {
    String selectedTimeStartTimeFormat,selectedTimeEndTimeFormat;
    String selected_vehicle_type;
    PlaceModel selectedStation;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Calendar calendar;

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

        //set value
        station_name.setText(selectedStation.getPlace_name());
        timing.setText( "Rs "+selectedStation.getUnit_rate()+" Per KM");


        Button btn_payment = findViewById(R.id.save_info);
        start_time.setOnClickListener(v -> showDateTimePicker(start_time));

        end_time.setOnClickListener(v -> showDateTimePicker(end_time));


        btn_payment.setOnClickListener(v -> {
            try {
                String startDateTimeStr = start_time.getText().toString().trim();
                String endDateTimeStr = end_time.getText().toString().trim();
                if (startDateTimeStr.isEmpty() || endDateTimeStr.isEmpty()) {
                    displayAlertDialog();
                } else {
                    long totalTimeInMinutes = calculateTotalMinutes(startDateTimeStr, endDateTimeStr);
                        double price = Double.parseDouble(selectedStation.getUnit_rate()) * (totalTimeInMinutes / 60.0);
                        if (price > 0) {
                            Intent i = createPaymentIntent(price,String.valueOf(totalTimeInMinutes));
                            startActivity(i);
                        } else {
                            instruction.setText("error is in timeSlot");
                        }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.backBtn_booking).setOnClickListener(v-> finish());
    }

    private void showDateTimePicker(final TextView editText) {
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    // Create a calendar instance for comparison with the selected date
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year1, month1, dayOfMonth);

                    // Check if the selected date is not earlier than the current date
                    if (selectedCalendar.compareTo(Calendar.getInstance()) >= 0) {
                        calendar.set(Calendar.YEAR, year1);
                        calendar.set(Calendar.MONTH, month1);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        @SuppressLint("SetTextI18n") TimePickerDialog timePickerDialog = new TimePickerDialog(
                                BookStation.this,
                                (view1, hourOfDay, minute1) -> {
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    calendar.set(Calendar.MINUTE, minute1);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                    String dateStr = dateFormat.format(calendar.getTime());
                                    String timeStr = timeFormat.format(calendar.getTime());
                                    editText.setText(dateStr + " " + timeStr);
                                },
                                hour,
                                minute,
                                true
                        );

                        timePickerDialog.show();
                    } else {
                        Toast.makeText(this, "Please select a date in the future", Toast.LENGTH_SHORT).show();
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }



    private void displayAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please fill all the details");
        builder.setCancelable(false);
        builder.setNegativeButton("Retry", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Helper method to create payment intent with necessary extras
    private Intent createPaymentIntent(double price,String duration) throws ParseException {
        Intent i = new Intent(this, PaymentActivity.class);
        i.putExtra("price", new DecimalFormat("##.##").format(price));
        i.putExtra("StationModel", selectedStation);
        i.putExtra("start_time", selectedTimeStartTimeFormat);
        i.putExtra("end_time", selectedTimeEndTimeFormat);
        i.putExtra("vehicle_type", selected_vehicle_type);
        i.putExtra("unit_con", String.valueOf(selectedStation.getUnit_rate()));
        i.putExtra("duration", duration);
        return i;
    }

    private long calculateTotalMinutes(String startTimeStr, String endTimeStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date startTime = format.parse(startTimeStr);
            Date endTime = format.parse(endTimeStr);
            if(endTime != null&&startTime != null){
                long differenceInMillis = endTime.getTime() - startTime.getTime();
                return differenceInMillis / (60 * 1000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}