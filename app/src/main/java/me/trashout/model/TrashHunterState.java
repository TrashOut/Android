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

package me.trashout.model;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.model
 * @since 01.03.2017
 */
public class TrashHunterState {

    private long startTime;
    private long trashHunterDuration; // 10min, 30min, 60min

    private int areaSize; // 500m (default), 1000m, 5000m, 20000m
    private long updateStatusTime; // 500m → 5 min., 1km → 10 min., 5km → 10 min., 20km → 15 min.


    public TrashHunterState(long trashHunterDuration, int areaSize) {
        this.trashHunterDuration = trashHunterDuration;
        this.areaSize = areaSize;

        this.startTime = System.currentTimeMillis();

        // Todo delete comment
        switch (this.areaSize) {
            case 1000:
            case 5000:
                this.updateStatusTime = 10 */* 60 **/ 1000;
                break;
            case 20000:
                this.updateStatusTime = 15 */* 60 **/ 1000;
                break;
            default:
                this.updateStatusTime = 5 */* 60 **/ 1000;
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getTrashHunterDutration() {
        return trashHunterDuration;
    }

    public int getAreaSize() {
        return areaSize;
    }

    public long getUpdateStatusTime() {
        return updateStatusTime;
    }

    public boolean isTrashHunterActive() {
        long actualTime = System.currentTimeMillis();
        return actualTime > this.startTime && actualTime < (startTime + trashHunterDuration);
    }

    public long getTrashHunterRemainingTime() {
        return (startTime + trashHunterDuration) - System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "TrashHunterState{" +
                "startTime=" + startTime +
                ", trashHunterDuration=" + trashHunterDuration +
                ", areaSize=" + areaSize +
                ", updateStatusTime=" + updateStatusTime +
                '}';
    }
}
