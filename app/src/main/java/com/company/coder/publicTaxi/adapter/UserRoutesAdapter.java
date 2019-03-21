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

import java.util.List;

public class UserRoutesAdapter extends RecyclerView.Adapter<UserRoutesAdapter.MyViewHolder> {


    private List<Vehicle> mArrayList;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Vehicle item);
    }

    private final OnItemClickListener listener;

    public UserRoutesAdapter(List<Vehicle> vehiclesList, Context context, OnItemClickListener listener) {
        this.mArrayList = vehiclesList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_route_item, parent, false);
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
        TextView pickUp, destination;

        MyViewHolder(View view) {
            super(view);
            pickUp = view.findViewById(R.id.route_item_PickUpName);
            destination = view.findViewById(R.id.route_item_destName);
        }

        void bind(final Vehicle vehicle, final OnItemClickListener listener) {
            pickUp.setText(vehicle.getmStartName());
            destination.setText(vehicle.getmEndName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(vehicle);
                }
            });
        }
    }

}
