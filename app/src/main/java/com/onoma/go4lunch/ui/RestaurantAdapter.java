package com.onoma.go4lunch.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.onoma.go4lunch.databinding.FragmentListItemBinding;
import com.onoma.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantAdapter extends ListAdapter<Restaurant, RestaurantAdapter.ViewHolder> {

    // private List<Restaurant> restaurantList = new ArrayList<>();
    private double locationLongitude;
    private double locationLatitude;
    private RestaurantDisplayCallback callback;

    private FragmentListItemBinding itemBinding;

    public RestaurantAdapter(double locationLongitude, double locationLatitude, RestaurantDisplayCallback callback) {
        super(RestaurantAdapter.DIFF_CALLBACK);
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position), callback);
        /*Restaurant restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAdress.setText(restaurant.getAdress());
        holder.restaurantDistance.setText(String.valueOf(restaurant.getLongitude()));*/
    }

    public static final DiffUtil.ItemCallback<Restaurant> DIFF_CALLBACK = new DiffUtil.ItemCallback<Restaurant>() {
        @Override
        public boolean areItemsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem.equals(newItem);
        }
    };

    /*public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        // TODO Mettre ?? jour avec des m??thodes plus sp??cifiques
        notifyDataSetChanged();
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantName;
        private TextView restaurantAdress;
        private TextView restaurantDistance;

        public ViewHolder(FragmentListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            restaurantName = itemBinding.fragmentListItemTitle;
            restaurantAdress = itemBinding.fragmentListItemAdress;
            restaurantDistance = itemBinding.fragmentListDistance;
        }

        public void bindTo(Restaurant restaurant, RestaurantDisplayCallback callback) {
            restaurantName.setText(restaurant.getName());
            restaurantAdress.setText(restaurant.getAdress());
            restaurantDistance.setText(restaurant.getDistance(locationLongitude, locationLatitude));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onRestaurantClick(restaurant);
                    Log.i("ITEM SELEC", String.valueOf(restaurant));
                }
            });
        }
    }

    public interface RestaurantDisplayCallback {
        void onRestaurantClick(Restaurant restaurant);
    }
}
