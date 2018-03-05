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

package me.trashout.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import me.trashout.api.base.ApiBaseDataResult;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.api.result
 * @since 17.02.2017
 */
public class ApiGetTrashCountResult extends ApiBaseDataResult {

    @SerializedName("count")
    @Expose
    private int count;

    private int reportedCount;
    private int cleanedCount;

    public ApiGetTrashCountResult(int reportedCount, int cleanedCount) {
        this.reportedCount = reportedCount;
        this.cleanedCount = cleanedCount;
    }

    public int getCount() {
        return count;
    }

    public int getReportedCount() {
        return reportedCount;
    }

    public int getCleanedCount() {
        return cleanedCount;
    }
}
