package com.example.vadym.movieapp.api;

/**
 * Created by Vadym on 04.02.2018.
 */

public class ApiError {
    // TODO: 2/6/18 Переформатуй, юзаючи SerializedName, методи також. Щоб не підсвічувалося.
    private String status_code;
    private String status_message;

    public ApiError() {
    }

    public String getStatus_code() {
        return status_code;
    }

    public String getStatus_message() {
        return status_message;
    }
}
