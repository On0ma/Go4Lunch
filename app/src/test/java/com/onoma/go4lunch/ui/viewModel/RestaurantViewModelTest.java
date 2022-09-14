package com.onoma.go4lunch.ui.viewModel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.repository.RestaurantRepository;
import com.onoma.go4lunch.ui.repository.RestaurantRepositoryImpl;
import com.onoma.go4lunch.ui.utils.StateData;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantViewModelTest extends TestCase {

    private final Restaurant FAKE_RESTAURANT = new Restaurant(
            "poi.910533071688",
            "Pho 13",
            "135 avenue de Choisy",
            "vietnamese restaurant, vietnamese food, restaurant",
            2.359247,
            48.826993,
            2,
            5);
    private final Restaurant FAKE_RESTAURANT_2 = new Restaurant(
            "poi.180388638774",
            "Le Bambou",
            "70 rue Baudricourt",
            "asian restaurant, asian food, restaurant",
            2.36259,
            48.825139,
            2,
            0
    );
    private final List<Restaurant> FAKE_RESTAURANT_LIST = Arrays.asList(FAKE_RESTAURANT, FAKE_RESTAURANT_2);
    private final Double FAKE_LONGITUDE = 0.0;
    private final Double FAKE_LATITUDE = 0.0;
    private final String ERROR_MESSAGE = "error getting data";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RestaurantRepositoryImpl restaurantRepository;

    private RestaurantsViewModel restaurantViewModel;

    @Before
    public void setUp() {
        // Mock methods from repository
        restaurantViewModel = new RestaurantsViewModel(restaurantRepository);
    }

    @Test
    public void getRestaurants_ShouldCallOnSuccess() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantRepository.RestaurantQuery callback = (RestaurantRepository.RestaurantQuery) args[2];
                callback.restaurantApiResult(FAKE_RESTAURANT_LIST);
                return null;
            }
        }).when(restaurantRepository).getRestaurants(anyDouble(), anyDouble(), any());

        restaurantViewModel.initRestaurants();
        StateData<List<Restaurant>> restaurantListData = restaurantViewModel.getRestaurants(FAKE_LONGITUDE, FAKE_LATITUDE).getValue();
        assertEquals(restaurantListData.getStatus(), StateData.DataStatus.SUCCESS);
        assertEquals(restaurantListData.getData(), FAKE_RESTAURANT_LIST);
        assertNull(restaurantListData.getError());
    }

    @Test
    public void getRestaurants_ShouldCallOnFailure() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantRepository.RestaurantQuery callback = (RestaurantRepository.RestaurantQuery) args[2];
                callback.restaurantApiFailure(ERROR_MESSAGE);
                return null;
            }
        }).when(restaurantRepository).getRestaurants(anyDouble(), anyDouble(), any());

        restaurantViewModel.initRestaurants();
        StateData<List<Restaurant>> restaurantListData = restaurantViewModel.getRestaurants(FAKE_LONGITUDE, FAKE_LATITUDE).getValue();
        assertEquals(restaurantListData.getStatus(), StateData.DataStatus.ERROR);
        assertNull(restaurantListData.getData());
        assertEquals(restaurantListData.getError(), ERROR_MESSAGE);
    }

    @Test
    public void deleteRestaurantFromUserFavorite() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantRepository.RestaurantFavoriteQuery callback = (RestaurantRepository.RestaurantFavoriteQuery) args[2];
                callback.getRestaurantFavorite(RestaurantRepository.RestaurantFavoriteResult.DELETE);
                return null;
            }
        }).when(restaurantRepository).updateRestaurantFavorite(any(),eq(true), any());

        restaurantViewModel.initRestaurantFavorite(FAKE_RESTAURANT, true);
        RestaurantRepository.RestaurantFavoriteResult restaurantFavoriteResultLiveData = restaurantViewModel.getRestaurantFavorite().getValue();
        assertEquals(restaurantFavoriteResultLiveData, RestaurantRepository.RestaurantFavoriteResult.DELETE);
    }

    @Test
    public void addRestaurantFromUserFavorite() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                RestaurantRepository.RestaurantFavoriteQuery callback = (RestaurantRepository.RestaurantFavoriteQuery) args[2];
                callback.getRestaurantFavorite(RestaurantRepository.RestaurantFavoriteResult.ADD);
                return null;
            }
        }).when(restaurantRepository).updateRestaurantFavorite(any(),eq(true), any());

        restaurantViewModel.initRestaurantFavorite(FAKE_RESTAURANT, true);
        RestaurantRepository.RestaurantFavoriteResult restaurantFavoriteResultLiveData = restaurantViewModel.getRestaurantFavorite().getValue();
        assertEquals(restaurantFavoriteResultLiveData, RestaurantRepository.RestaurantFavoriteResult.ADD);
    }
}
