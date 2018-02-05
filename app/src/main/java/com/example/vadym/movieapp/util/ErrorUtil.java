package com.example.vadym.movieapp.util;

import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.api.MovieRetrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by Vadym on 04.02.2018.
 */

public class ErrorUtil {

    public static ApiError parseError(Response<?> response){

        Converter<ResponseBody,ApiError> converter = MovieRetrofit
                .getReferenceRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }

        return error;
    }
}
