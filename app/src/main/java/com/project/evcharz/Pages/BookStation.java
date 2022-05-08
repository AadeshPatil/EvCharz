package com.project.evcharz.Pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.evcharz.Model.PlaceModel;
import com.project.evcharz.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_staion);

        selectedStation = (PlaceModel) getIntent().getSerializableExtra("StationModel");

        TextView start_time = findViewById(R.id.start_time);
        TextView end_time = findViewById(R.id.end_time);

        TextView station_name = findViewById(R.id.station_name_booking);
        TextView timing = findViewById(R.id.timing_booking_page);

        TextView instruction = findViewById(R.id.instruction);

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
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                String min = selectedMinute+"";



                if(min.trim().length() == 1){
                    min = 0 + ""+ min;
                }
                selectedTimeStartTimeFormat = selectedHour+":"+min+":"+"00";

                if(selectedHour>=0 && selectedHour<12){
                    SelectedTimeStart = selectedHour + " : " + min + " AM";
                } else {
                    if (selectedHour != 12) {
                        selectedHour = selectedHour - 12;
                    }
                    SelectedTimeStart = selectedHour + " : " + min + " PM";
                }
                start_time.setText(SelectedTimeStart);
            }, hour, minute, true);
            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();
        });

        end_time.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {


                String min = selectedMinute+"";


                if(min.trim().length() == 1){
                    min = 0 + ""+ min;
                }
                selectedTimeEndTimeFormat = selectedHour+":"+min+":"+"00";
                Log.d("time",selectedTimeEndTimeFormat);

                if(selectedHour>=0 && selectedHour<12){
                    SelectedTimeEnd = selectedHour + " : " + min + " AM";

                } else {
                    if (selectedHour != 12) {
                        selectedHour = selectedHour - 12;
                    }
                    SelectedTimeEnd = selectedHour + " : " + min + " PM";
                }
                end_time.setText(SelectedTimeEnd);
            }, hour, minute, true);
            mTimePicker.setTitle("Select Start Time");
            mTimePicker.show();
        });

        btn_payment.setOnClickListener(v->{
            try {

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

        double rate =  selected_vehicle_rate*unit_rate;
        return rate;
    }

    private long checkDuration() throws ParseException {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date startDate = simpleDateFormat.parse(selectedTimeStartTimeFormat);
        Date endDate = simpleDateFormat.parse(selectedTimeEndTimeFormat);

        assert startDate != null;
        assert endDate != null;
        long difference = endDate.getTime() - startDate.getTime();
        if(difference<0)
        {
            Date dateMax = simpleDateFormat.parse("24:00");
            Date dateMin = simpleDateFormat.parse("00:00");
            assert dateMax != null;
            difference=(dateMax.getTime() -startDate.getTime() )+(endDate.getTime()-dateMin.getTime());
        }
        int days = (int) (difference / (1000*60*60*24));
        int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        int min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        Log.i("log_tag","Hours: "+hours+", Mins: "+min);

        if (hours != 0){
            return hours* 60L;
        }else{
            return min;
        }
    }

}