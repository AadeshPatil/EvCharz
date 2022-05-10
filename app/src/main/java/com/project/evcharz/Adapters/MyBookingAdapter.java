package com.project.evcharz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public void onBindViewHolder(@NonNull MyBookingAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        BookingModel model = BookingModelArrayList.get(position);



        holder.booking_date_time.setText(model.getDate() + " | "+ model.getStart_time() +"-"+model.getEnd_time());
        holder.station_name.setText("" + model.getStation_name());
        holder.amount_paid.setText("Rs. "+model.getAmount_paid());
        holder.status.setText(model.getStatus());

        if (model.getVehicle_type().equals("bike")){
            holder.vehicle_type_icon.setImageResource(R.drawable.bike_marker);
        }else if(model.getVehicle_type().equals("car")) {
            holder.vehicle_type_icon.setImageResource(R.drawable.car_icon);
        }else {
            holder.vehicle_type_icon.setImageResource(R.drawable.auto_icon);
        }
    }

    @Override
    public int getItemCount() {
        return BookingModelArrayList.size();
    }
    
    public class Viewholder extends RecyclerView.ViewHolder {
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
