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

package me.trashout.api.request;

import me.trashout.api.base.ApiBaseRequest;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.api.request
 * @since 02.12.2016
 */
public class ApiGetUserByFirebaseTokenRequest extends ApiBaseRequest {

    private boolean signUp;
    private String firtName;
    private String lastName;
    private String email;
    private String facebookToken;

    public ApiGetUserByFirebaseTokenRequest(int id, boolean signUp, String firtName, String lastName, String email, String facebookToken) {
        super(id);
        this.signUp = signUp;
        this.firtName = firtName;
        this.lastName = lastName;
        this.email = email;
        this.facebookToken = facebookToken;
    }

    public boolean isSignUp() {
        return signUp;
    }

    public String getFirtName() {
        return firtName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getFacebookToken() {
        return facebookToken;
    }
}
