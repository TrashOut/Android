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

public class ApiBaseRequest {

    public transient static final long MAX_REPEATED_REQUEST_TIME = 45 * 1000; // max reteated request time - 45s

    /**
     * Reqest status
     */
    public enum Status {
        NEW, PENDING, DONE, NOT_IN_QUEUE, ERROR;
    }

    private transient final int id;
    private transient long timestamp;
    protected transient Status status;

    public ApiBaseRequest(int id) {
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.status = Status.NEW;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isRepeatedRequestPossible() {
        return this.timestamp + MAX_REPEATED_REQUEST_TIME > System.currentTimeMillis();
    }

    @Override
    public int hashCode() {
        return (String.valueOf(id)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (((Object) this).getClass() != ((Object) obj).getClass()) {
            return false;
        }
        ApiBaseRequest other = (ApiBaseRequest) obj;

        if (id != other.id) {
            return false;
        }

        return true;
    }

}
