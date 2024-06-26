package com.project.mySite.component.Utils;

public class ServiceResult<T> {

    private T data;
    private String errorMessage;
    private boolean success;


    public ServiceResult() {
    }

    public static <T> ServiceResult<T> success(T data) {
        ServiceResult<T> result = new ServiceResult<T>();
        result.data = data;
        result.success = true;
        return result;
    }

    public static <T> ServiceResult<T> failure(String message) {
        ServiceResult<T> result = new ServiceResult<T>();
        result.errorMessage = message;
        result.success = false;
        return result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
