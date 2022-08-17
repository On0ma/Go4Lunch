package com.onoma.go4lunch.ui.utils;

import androidx.lifecycle.MutableLiveData;

public class StateLiveData<T> extends MutableLiveData<StateData<T>> {

    public void postError(String error) {
        postValue(new StateData<T>().error(error));
    }

    public void postSuccess(T data) {
        postValue(new StateData<T>().success(data));
    }
}
