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
import com.onoma.go4lunch.databinding.FragmentWorkmatesItemBinding;
import com.onoma.go4lunch.model.User;
import com.squareup.picasso.Picasso;


public class WorkmateAdapter  extends ListAdapter<User, WorkmateAdapter.ViewHolder> {

    private FragmentWorkmatesItemBinding itemBinding;

    public WorkmateAdapter() {
        super(WorkmateAdapter.DIFF_CALLBACK);
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

        public ViewHolder(FragmentWorkmatesItemBinding itemBinding) {
            super(itemBinding.getRoot());
            workersName = itemBinding.workmateRestaurant;
            workersPicture = itemBinding.workmatePicture;
        }

        public void bindTo(User user) {
            workersName.setText(user.getName());
            /*Glide.with(workersPicture.getContext())
                    .load(user.getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(workersPicture);*/
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .into(workersPicture);
        }
    }
}

