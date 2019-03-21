package com.company.coder.publicTaxi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.company.coder.publicTaxi.R;
import com.company.coder.publicTaxi.modles.Vehicle;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder> {


    private List<Vehicle> mArrayList;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Vehicle item);
    }

    private final OnItemClickListener listener;

    public VehicleAdapter(List<Vehicle> vehiclesList, Context context, OnItemClickListener listener) {
        this.mArrayList = vehiclesList;
        this.context = context;
        this.listener = listener;
//        Log.d("OwnerHomeScreen", "Adapter Size" + mArrayList.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.driver_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(mArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView number, model, make;
        CircleImageView imageView;

        MyViewHolder(View view) {
            super(view);
            number = view.findViewById(R.id.vehicle_item_number);
            model = view.findViewById(R.id.vehicle_item_model);
            make = view.findViewById(R.id.vehicle_item_make);
            imageView = view.findViewById(R.id.vehicle_item_image);
        }

        void bind(final Vehicle vehicle, final OnItemClickListener listener) {
            number.setText(vehicle.getmPlateNumber());
            model.setText(vehicle.getmModel());
            make.setText(vehicle.getmMake());
            if (!vehicle.getmImgUrl().equals("")) {
                Picasso.with(context)
                        .load(vehicle.getmImgUrl())
                        .placeholder(R.drawable.progress_animation)
                        .into(imageView);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(vehicle);
                }
            });
        }
    }
}
