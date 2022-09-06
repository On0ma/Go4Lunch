package com.onoma.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.onoma.go4lunch.ui.repository.RestaurantRepositoryImpl;
import com.onoma.go4lunch.ui.repository.UserRepositoryImpl;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    private final UserRepositoryImpl userRepository = new UserRepositoryImpl();
    private final RestaurantRepositoryImpl restaurantRepository = new RestaurantRepositoryImpl();

    private ViewModelFactory() {
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            // We inject the Repository in the ViewModel constructor
            return (T) new UserViewModel(userRepository);
        }
        if (modelClass.isAssignableFrom(RestaurantsViewModel.class)) {
            // We inject the Repository in the ViewModel constructor
            return (T) new RestaurantsViewModel(restaurantRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
