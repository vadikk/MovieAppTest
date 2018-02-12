package com.example.vadym.movieapp.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Vadym on 04.02.2018.
 */

public class ApiError {

    @SerializedName("status_code")
    private String code;
    @SerializedName("status_message")
    private String message;

    public ApiError(String mes) {
        message = mes;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
