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

package com.beoui.geocell.model;

/**
 * Interface to create a cost function used in geocells algorithm.
 * This function will determine the cost of an operation depending of number of cells and resolution.
 * When the cost is going higher, the algorithm stops.
 * The cost depends on application use of geocells.
 *
 * @author Alexandre Gellibert
 *
 */
public interface CostFunction {

    /**
     * @param numCells number of cells found
     * @param resolution resolution of those cells
     * @return the cost of the operation
     */
    public double defaultCostFunction(int numCells, int resolution);

}
