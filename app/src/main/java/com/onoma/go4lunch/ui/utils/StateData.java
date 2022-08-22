package com.onoma.go4lunch.ui.utils;

public class StateData<T> {
    private DataStatus status;
    private T data;
    private String error;

    // TODO remove created status
    public StateData() {
        this.status = null;
        this.data = null;
        this.error = null;
    }

    public StateData<T> success(T data) {
        this.status = DataStatus.SUCCESS;
        this.data = data;
        this.error = null;
        return this;
    }

    public StateData<T> error(String error) {
        this.status = DataStatus.ERROR;
        this.data = null;
        this.error = error;
        return this;
    }

    public DataStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    public enum DataStatus {
        SUCCESS,
        ERROR
    }
}
