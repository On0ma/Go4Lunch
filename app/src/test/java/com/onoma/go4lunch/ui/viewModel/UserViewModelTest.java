package com.onoma.go4lunch.ui.viewModel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.onoma.go4lunch.ui.repository.UserRepositoryImpl;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserViewModelTest extends TestCase {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepositoryImpl userRepository;

    private UserViewModel userViewModel;

    @Before
    public void setUp() {
        // Reinitialize liveData every test
        MutableLiveData<Boolean> userLoggedBoolean = new MutableLiveData<Boolean>();
        // Mock methods from repository
        userViewModel = new UserViewModel();
    }

    @Test
    public void getUserData() {

    }
}