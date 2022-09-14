package com.onoma.go4lunch.ui;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentListItemBinding;
import com.onoma.go4lunch.model.Restaurant;

import java.util.Objects;

public class RestaurantAdapter extends ListAdapter<Restaurant, RestaurantAdapter.ViewHolder> {

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
    }

    public static final DiffUtil.ItemCallback<Restaurant> DIFF_CALLBACK = new DiffUtil.ItemCallback<Restaurant>() {
        @Override
        public boolean areItemsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return oldItem.getNbSelection() == newItem.getNbSelection();
        }

        @Override
        public Object getChangePayload(@NonNull Restaurant oldItem, @NonNull Restaurant newItem) {
            return null;
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView restaurantName;
        private TextView restaurantAdress;
        private TextView restaurantDistance;
        private TextView restaurantWorkersNb;
        private ImageView restaurantStar1;
        private ImageView restaurantStar2;
        private ImageView restaurantStar3;

        public ViewHolder(FragmentListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            restaurantName = itemBinding.fragmentListItemTitle;
            restaurantAdress = itemBinding.fragmentListItemAdress;
            restaurantDistance = itemBinding.fragmentListDistance;
            restaurantWorkersNb = itemBinding.fragmentListWorkers;
            restaurantStar1 = itemBinding.fragmentListRating1;
            restaurantStar2 = itemBinding.fragmentListRating2;
            restaurantStar3 = itemBinding.fragmentListRating3;
        }

        public void bindTo(Restaurant restaurant, RestaurantDisplayCallback callback) {
            restaurantName.setText(restaurant.getName());
            restaurantAdress.setText(restaurant.getAdress());
            restaurantDistance.setText(restaurant.getDistance(locationLongitude, locationLatitude));
            restaurantWorkersNb.setText("("+restaurant.getNbSelection()+")");

            Drawable starFilled = restaurantStar1.getContext().getDrawable(R.drawable.ic_baseline_star_24);
            Drawable starOutline = restaurantStar1.getContext().getDrawable(R.drawable.ic_baseline_star_outline_24);
            int favoriteNb = restaurant.getNbFavorite();

            if (favoriteNb >= 10) {
                // 3 stars
                restaurantStar1.setImageDrawable(starFilled);
                restaurantStar2.setImageDrawable(starFilled);
                restaurantStar3.setImageDrawable(starFilled);
            } else if (favoriteNb >=5 && favoriteNb < 10 ) {
                // 2 stars
                restaurantStar1.setImageDrawable(starFilled);
                restaurantStar2.setImageDrawable(starFilled);
                restaurantStar3.setImageDrawable(starOutline);
            } else if (favoriteNb > 0 && favoriteNb < 5) {
                // 1 stars
                restaurantStar1.setImageDrawable(starFilled);
                restaurantStar2.setImageDrawable(starOutline);
                restaurantStar3.setImageDrawable(starOutline);
            } else {
                // 0 stars
                restaurantStar1.setImageDrawable(starOutline);
                restaurantStar2.setImageDrawable(starOutline);
                restaurantStar3.setImageDrawable(starOutline);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onRestaurantClick(restaurant);
                }
            });
        }
    }

    public interface RestaurantDisplayCallback {
        void onRestaurantClick(Restaurant restaurant);
    }
}
