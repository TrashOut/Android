/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.api.base;

import retrofit2.Response;

public class ApiResult {

    private int requestId;
    private ApiBaseDataResult result;
    private ApiBaseRequest request;
    private Response response;
    private ApiError apiError;
    private Throwable exception;

    public ApiResult(int requestId, ApiBaseRequest request, ApiBaseDataResult result, Response response, Throwable exception) {
        this.requestId = requestId;
        this.result = result;
        this.request = request;
        this.response = response;
        this.exception = exception;
        this.apiError = parseApiError();
    }

    public int getRequestId() {
        return requestId;
    }

    public Object getResult() {
        return result;
    }

    public Response getResponse() {
        return response;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isValidResponse() {
        return exception == null && (response == null || (response.errorBody() == null && response.isSuccessful())) && apiError == null;
    }

    public ApiBaseRequest getRequest() {
        return request;
    }


    public boolean isApiError() {
        return apiError != null;
    }

    public ApiError getApiError() {
        return apiError;
    }

    private ApiError parseApiError() {
        if (response != null && !response.isSuccessful()) {
            ApiError apiError = new ApiError();
            apiError.setError(response.errorBody().toString());
            apiError.setErrorNumber(response.code());
            return apiError;
        }else if(result != null ){
            return result.getApiError();
        }
        return null;
    }

    public int getErrorCode() {
        if (result != null && result.getApiError() != null)
            return result.getApiError().getErrorNumber();
        else
            return 500;
    }
}
