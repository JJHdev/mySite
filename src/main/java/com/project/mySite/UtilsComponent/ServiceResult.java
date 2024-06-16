package com.project.mySite.UtilsComponent;

public class ServiceResult<T> {

    private T data;
    private String errorMessage;
    private boolean success;

    public ServiceResult(T data) {
        this.data = data;
        this.success = true;
    }

    public ServiceResult(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(data);
    }

    public static <T> ServiceResult<T> failure(String message) {
        return new ServiceResult<T>(message);
    }
}
