package com.project.evcharz.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.collection.LLRBNode;
import com.project.evcharz.Model.BookingModel;
import com.project.evcharz.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.Viewholder> {

    private final ArrayList<BookingModel> BookingModelArrayList;

    // Constructor
    public MyBookingAdapter(Context context, ArrayList<BookingModel> BookingModelArrayList) {
        this.BookingModelArrayList = BookingModelArrayList;
    }

    @NonNull
    @Override
    public MyBookingAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_card_layout, parent, false);
        return new Viewholder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyBookingAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        BookingModel model = BookingModelArrayList.get(position);


        String dateTimeRange = model.getDate() + " | " + model.getStart_time() + "-" + model.getEnd_time();
        holder.booking_date_time.setText(dateTimeRange);
        holder.station_name.setText(model.getStation_name());
        holder.amount_paid.setText("Rs. " + model.getAmount_paid());

        holder.status.setText(model.getStatus());
//        holder.vehicle_type_icon.setImageResource(R.drawable.bike_marker);

        if (model.getStatus().equals("Canceled")){
            holder.status.setTextColor(Color.RED);
        }
        if (model.getVehicle_type().equals("bike")){
            holder.vehicle_type_icon.setImageResource(R.drawable.electric_bike);
        }else if(model.getVehicle_type().equals("car")) {
            holder.vehicle_type_icon.setImageResource(R.drawable.electric_car);
        }else {
            holder.vehicle_type_icon.setImageResource(R.drawable.local_taxi);
        }
    }

    @Override
    public int getItemCount() {
        return BookingModelArrayList.size();
    }
    
    public static class Viewholder extends RecyclerView.ViewHolder {
        private final ImageView vehicle_type_icon;
        private final TextView booking_date_time;
        private final TextView station_name;
        private final TextView amount_paid;
        private final TextView status;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            vehicle_type_icon = itemView.findViewById(R.id.vehicle_type_ico);
            booking_date_time = itemView.findViewById(R.id.booking_date_time);
            station_name = itemView.findViewById(R.id.station_name);
            amount_paid = itemView.findViewById(R.id.total_paid_amount);
            status = itemView.findViewById(R.id.booking_curr_status);
        }
    }
}
