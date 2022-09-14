package com.onoma.go4lunch.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.ActivityRestaurantItemBinding;
import com.onoma.go4lunch.model.User;

public class RestaurantWorkersAdapter extends ListAdapter<User, RestaurantWorkersAdapter.ViewHolder> {

    private ActivityRestaurantItemBinding itemBinding;

    public RestaurantWorkersAdapter() {
        super(RestaurantWorkersAdapter.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RestaurantWorkersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = ActivityRestaurantItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestaurantWorkersAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantWorkersAdapter.ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
    }

    public static final DiffUtil.ItemCallback<User> DIFF_CALLBACK = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUid() == newItem.getUid();
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.equals(newItem);
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView workerPicture;
        private TextView workerName;

        public ViewHolder(ActivityRestaurantItemBinding itemBinding) {
            super(itemBinding.getRoot());
            workerPicture = itemBinding.restaurantWorkerItemImage;
            workerName = itemBinding.restaurantWorkerItemName;
        }

        public void bindTo(User user) {
            workerName.setText(workerName.getResources().getString(R.string.workmates_restaurant, user.getName()));
            Glide.with(workerPicture.getContext())
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_baseline_person_24_black)
                    .into(workerPicture);
        }
    }
}
