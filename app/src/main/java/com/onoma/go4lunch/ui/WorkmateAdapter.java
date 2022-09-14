package com.onoma.go4lunch.ui;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentWorkmatesItemBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;


public class WorkmateAdapter  extends ListAdapter<User, WorkmateAdapter.ViewHolder> {

    private FragmentWorkmatesItemBinding itemBinding;
    private List<Restaurant> restaurantList = new ArrayList<>();

    public WorkmateAdapter(List<Restaurant> restaurantList) {
        super(WorkmateAdapter.DIFF_CALLBACK);
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public WorkmateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = FragmentWorkmatesItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmateAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateAdapter.ViewHolder holder, int position) {
        holder.bindTo(getItem(position));

        if (position == getCurrentList().size() - 1) {
            holder.lastItemModification();
        }
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
        private TextView workersName;
        private ImageView workersPicture;
        private View itemDivider;

        public ViewHolder(FragmentWorkmatesItemBinding itemBinding) {
            super(itemBinding.getRoot());
            workersName = itemBinding.workmateRestaurant;
            workersPicture = itemBinding.workmatePicture;
            itemDivider = itemBinding.workmateListSeparator;
        }

        public void bindTo(User user) {
            itemDivider.setVisibility(View.VISIBLE);
            workersName.setText(user.getName());
            Glide.with(workersPicture.getContext())
                    .load(user.getPhotoUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.drawable.ic_baseline_person_24_black)
                    .into(workersPicture);

            if (!user.getRestaurantId().isEmpty()) {
                String restaurantName = "";
                for (Restaurant restaurant : restaurantList) {
                    if (user.getRestaurantId().equals(restaurant.getId())) {
                        restaurantName = restaurant.getName();
                    }
                }
                workersName.setText(workersName.getResources().getString(R.string.workmates_choice, user.getName(), restaurantName));
                workersName.setTextColor(workersName.getResources().getColor(R.color.black));
                workersName.setTypeface(workersName.getTypeface(), Typeface.NORMAL);
            } else {
                workersName.setText(workersName.getResources().getString(R.string.workmates_no_choice, user.getName()));
                workersName.setTextColor(workersName.getResources().getColor(R.color.text_secondary));
                workersName.setTypeface(workersName.getTypeface(), Typeface.ITALIC);
            }
        }

        public void lastItemModification() {
            itemDivider.setVisibility(View.INVISIBLE);
        }
    }
}

