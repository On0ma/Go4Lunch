package com.onoma.go4lunch.ui.viewModel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.UserRepository;
import com.onoma.go4lunch.ui.repository.UserRepositoryImpl;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserViewModelTest extends TestCase {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepositoryImpl userRepository;

    private UserViewModel userViewModel;

    private final User FAKE_USER = new User(
            "1",
            "Victoria Watkins",
            "victoria.watkins@example.com",
            "https://randomuser.me/api/portraits/women/20.jpg");
    private final User FAKE_USER_2 = new User(
            "2",
            "Steven Armstrong",
            "steven.armstrong@example.com",
            "https://randomuser.me/api/portraits/men/10.jpg");
    private final List<User> FAKE_USERS_LIST = Arrays.asList(FAKE_USER, FAKE_USER_2);
    private final Restaurant FAKE_RESTAURANT = new Restaurant(
            "poi.910533071688",
            "Pho 13",
            "135 avenue de Choisy",
            "vietnamese restaurant, vietnamese food, restaurant",
            2.359247,
            48.826993,
            2,
            5);
    private final String ERROR_MESSAGE = "error getting data";

    @Before
    public void setUp() {
        // Mock methods from repository
        userViewModel = new UserViewModel(userRepository);
    }


    @Test
    public void getUserData_shouldCallOnSuccess() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.UserQuery callback = (UserRepository.UserQuery) args[0];
                callback.getUserSuccess(FAKE_USER);
                return null;
            }
        }).when(userRepository).getUserData(any());

        StateData userData = userViewModel.getUserData().getValue();
        assertEquals(userData.getStatus(), StateData.DataStatus.SUCCESS);
        assertEquals(userData.getData(), FAKE_USER);
        assertNull(userData.getError());
    }

    @Test
    public void getUserData_shouldCallOnFailure() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.UserQuery callback = (UserRepository.UserQuery) args[0];
                callback.getUserFailure(ERROR_MESSAGE);
                return null;
            }
        }).when(userRepository).getUserData(any());

        StateData userData = userViewModel.getUserData().getValue();
        assertEquals(userData.getStatus(), StateData.DataStatus.ERROR);
        assertNull(userData.getData());
        assertEquals(userData.getError(), ERROR_MESSAGE);
    }

    @Test
    public void getCurrentUser_ShouldBeTrue() {
        Mockito.when(userRepository.getIsUserLoggedIn()).thenReturn(true);
        Boolean isUserLoggedIn = userViewModel.isCurrentUserLogged().getValue();
        assertTrue(isUserLoggedIn);
    }

    @Test
    public void getAllUsers_ShouldCallOnSuccess() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.AllUsersQuery callback = (UserRepository.AllUsersQuery) args[0];
                callback.getAllUsersSuccess(FAKE_USERS_LIST);
                return null;
            }
        }).when(userRepository).getAllUsers(any());

        StateData userListData = userViewModel.getAllUsers().getValue();
        assertEquals(userListData.getStatus(), StateData.DataStatus.SUCCESS);
        assertEquals(userListData.getData(), FAKE_USERS_LIST);
        assertNull(userListData.getError());
    }

    @Test
    public void getAllUsers_ShouldCallOnFailure() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.AllUsersQuery callback = (UserRepository.AllUsersQuery) args[0];
                callback.getAllUsersFailure(ERROR_MESSAGE);
                return null;
            }
        }).when(userRepository).getAllUsers(any());

        StateData userListData = userViewModel.getAllUsers().getValue();
        assertEquals(userListData.getStatus(), StateData.DataStatus.ERROR);
        assertNull(userListData.getData());
        assertEquals(userListData.getError(), ERROR_MESSAGE);
    }

    @Test
    public void getAllUsersFromRestaurant_ShouldCallOnSuccess() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.AllUsersQuery callback = (UserRepository.AllUsersQuery) args[1];
                callback.getAllUsersSuccess(FAKE_USERS_LIST);
                return null;
            }
        }).when(userRepository).getAllUsersFromRestaurant(any(), any());

        userViewModel.initUsersFromRestaurant(FAKE_RESTAURANT);
        StateData userListData = userViewModel.getUsersFromRestaurant().getValue();
        assertEquals(userListData.getStatus(), StateData.DataStatus.SUCCESS);
        assertEquals(userListData.getData(), FAKE_USERS_LIST);
        assertNull(userListData.getError());
    }

    @Test
    public void getAllUsersFromRestaurant_ShouldCallOnFailure() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.AllUsersQuery callback = (UserRepository.AllUsersQuery) args[1];
                callback.getAllUsersFailure(ERROR_MESSAGE);
                return null;
            }
        }).when(userRepository).getAllUsersFromRestaurant(any(), any());

        userViewModel.initUsersFromRestaurant(FAKE_RESTAURANT);
        StateData userListData = userViewModel.getUsersFromRestaurant().getValue();
        assertEquals(userListData.getStatus(), StateData.DataStatus.ERROR);
        assertNull(userListData.getData());
        assertEquals(userListData.getError(), ERROR_MESSAGE);
    }

    @Test
    public void getCurrentUserSelection_ShouldCallOnSuccess() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.UserSelectionQuery callback = (UserRepository.UserSelectionQuery) args[0];
                callback.getUserSelectionSuccess(FAKE_RESTAURANT);
                return null;
            }
        }).when(userRepository).getCurrentUserSelection(any());

        userViewModel.initUserSelection();
        StateData userListData = userViewModel.getUserSelection().getValue();
        assertEquals(userListData.getStatus(), StateData.DataStatus.SUCCESS);
        assertEquals(userListData.getData(), FAKE_RESTAURANT);
        assertNull(userListData.getError());
    }

    @Test
    public void getCurrentUserSelection_ShouldCallOnFailure() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                UserRepository.UserSelectionQuery callback = (UserRepository.UserSelectionQuery) args[0];
                callback.getUserSelectionFailure(ERROR_MESSAGE);
                return null;
            }
        }).when(userRepository).getCurrentUserSelection(any());

        userViewModel.initUserSelection();
        StateData userListData = userViewModel.getUserSelection().getValue();
        assertEquals(userListData.getStatus(), StateData.DataStatus.ERROR);
        assertNull(userListData.getData());
        assertEquals(userListData.getError(), ERROR_MESSAGE);
    }
}