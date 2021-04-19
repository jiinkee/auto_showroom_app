package com.example.autoshowroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.CarItemViewHolder> {
    Context context;
    ArrayList<Car> carsData;

    public CarListAdapter(Context ctx, ArrayList<Car> cars) {
        this.context = ctx;
        this.carsData = cars;
    }

    @NonNull
    @Override
    public CarItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.car_item_layout, parent, false);
        return new CarItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarItemViewHolder holder, int position) {
        holder.count.setText("Car " + (position + 1));
        Car car = carsData.get(position);
        holder.maker.setText(car.getMaker());
        holder.model.setText(car.getModel());
        holder.year.setText(car.getYearString());
        holder.color.setText(car.getColor());
        holder.seatNum.setText(car.getSeatNumString());
        holder.price.setText(car.getPriceString());

        holder.itemView.setOnClickListener(view -> {
            Toast.makeText(view.getContext(), "Car No." + (position + 1) + "with name:" +
                            car.getMaker() + " and model:" + car.getModel() + " is selected",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return carsData.size();
    }

    public class CarItemViewHolder extends RecyclerView.ViewHolder{
        private TextView count, maker, model, year, color, seatNum, price;
        public CarItemViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.txtCarCounter);
            maker = itemView.findViewById(R.id.txtCarMaker);
            model = itemView.findViewById(R.id.txtCarModel);
            year = itemView.findViewById(R.id.txtCarYear);
            color = itemView.findViewById(R.id.txtCarColor);
            seatNum = itemView.findViewById(R.id.txtCarSeatNum);
            price = itemView.findViewById(R.id.txtCarPrice);
        }
    }
}
