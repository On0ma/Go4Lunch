package com.onoma.go4lunch.ui;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentListItemBinding;
import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolder> {

    private List<Restaurant> restaurantList = new ArrayList<>();

    private FragmentListItemBinding itemBinding;

    public ListUserAdapter() { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = FragmentListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.restaurantName.setText(restaurant.getName());
        holder.restaurantAdress.setText(restaurant.getAdress());
        holder.restaurantDistance.setText(String.valueOf(restaurant.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        // TODO Mettre à jour avec des méthodes plus spécifiques
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantName;
        private TextView restaurantAdress;
        private TextView restaurantDistance;

        public ViewHolder(FragmentListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            restaurantName = itemBinding.fragmentListItemTitle;
            restaurantAdress = itemBinding.fragmentListItemAdress;
            restaurantDistance = itemBinding.fragmentListDistance;
        }
    }
}
