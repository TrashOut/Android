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

package com.beoui.geocell;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.beoui.geocell.model.BoundingBox;
import com.beoui.geocell.model.CostFunction;
import com.beoui.geocell.model.DefaultCostFunction;

/**
 * Ported java version of python geocell: http://code.google.com/p/geomodel/source/browse/trunk/geo/geocell.py
 *
 * Defines the notion of 'geocells' and exposes methods to operate on them.

 A geocell is a hexadecimal string that defines a two dimensional rectangular
 region inside the [-90,90] x [-180,180] latitude/longitude space. A geocell's
 'resolution' is its length. For most practical purposes, at high resolutions,
 geocells can be treated as single points.

 Much like geohashes (see http://en.wikipedia.org/wiki/Geohash), geocells are
 hierarchical, in that any prefix of a geocell is considered its ancestor, with
 geocell[:-1] being geocell's immediate parent cell.

 To calculate the rectangle of a given geocell string, first divide the
 [-90,90] x [-180,180] latitude/longitude space evenly into a 4x4 grid like so:

 +---+---+---+---+ (90, 180)
 | a | b | e | f |
 +---+---+---+---+
 | 8 | 9 | c | d |
 +---+---+---+---+
 | 2 | 3 | 6 | 7 |
 +---+---+---+---+
 | 0 | 1 | 4 | 5 |
 (-90,-180) +---+---+---+---+

 NOTE: The point (0, 0) is at the intersection of grid cells 3, 6, 9 and c. And,
 for example, cell 7 should be the sub-rectangle from
 (-45, 90) to (0, 180).

 Calculate the sub-rectangle for the first character of the geocell string and
 re-divide this sub-rectangle into another 4x4 grid. For example, if the geocell
 string is '78a', we will re-divide the sub-rectangle like so:

 .                   .
 .                   .
 . . +----+----+----+----+ (0, 180)
 | 7a | 7b | 7e | 7f |
 +----+----+----+----+
 | 78 | 79 | 7c | 7d |
 +----+----+----+----+
 | 72 | 73 | 76 | 77 |
 +----+----+----+----+
 | 70 | 71 | 74 | 75 |
 . . (-45,90) +----+----+----+----+
 .                   .
 .                   .

 Continue to re-divide into sub-rectangles and 4x4 grids until the entire
 geocell string has been exhausted. The final sub-rectangle is the rectangular
 region for the geocell.
 *
 * @author api.roman.public@gmail.com (Roman Nurik)
 * @author (java portage) Alexandre Gellibert
 *
 *
 */

public class GeocellManager {

    // The maximum *practical* geocell resolution.
    public static final int MAX_GEOCELL_RESOLUTION = 13;

    // The maximum number of geocells to consider for a bounding box search.
    private static final int MAX_FEASIBLE_BBOX_SEARCH_CELLS = 300;

    // Function used if no custom function is used in bestBboxSearchCells method
    private static final CostFunction DEFAULT_COST_FUNCTION = new DefaultCostFunction();

    /**
     * Returns the list of geocells (all resolutions) that are containing the point
     *
     * @param point
     * @return Returns the list of geocells (all resolutions) that are containing the point
     */
    public static List<String> generateGeoCell(LatLng point) {
        List<String> geocells = new ArrayList<String>();
        String geocellMax = GeocellUtils.compute(point, GeocellManager.MAX_GEOCELL_RESOLUTION);
        for (int i = 1; i < GeocellManager.MAX_GEOCELL_RESOLUTION; i++) {
            geocells.add(GeocellUtils.compute(point, i));
        }
        geocells.add(geocellMax);
        return geocells;
    }


    /**
     * Returns an efficient set of geocells to search in a bounding box query.

     This method is guaranteed to return a set of geocells having the same
     resolution (except in the case of antimeridian search i.e when east < west).

     * @param bbox: A geotypes.Box indicating the bounding box being searched.
     * @param costFunction: A function that accepts two arguments:
     * numCells: the number of cells to search
     * resolution: the resolution of each cell to search
    and returns the 'cost' of querying against this number of cells
    at the given resolution.)
     * @return A list of geocell strings that contain the given box.
     */
    public static List<String> bestBboxSearchCells(BoundingBox bbox, CostFunction costFunction) {
        if (bbox.getEast() < bbox.getWest()) {
            BoundingBox bboxAntimeridian1 = new BoundingBox(bbox.getNorth(), bbox.getEast(), bbox.getSouth(), GeocellUtils.MIN_LONGITUDE);
            BoundingBox bboxAntimeridian2 = new BoundingBox(bbox.getNorth(), GeocellUtils.MAX_LONGITUDE, bbox.getSouth(), bbox.getWest());
            List<String> antimeridianList = bestBboxSearchCells(bboxAntimeridian1, costFunction);
            antimeridianList.addAll(bestBboxSearchCells(bboxAntimeridian2, costFunction));
            return antimeridianList;
        }

        String cellNE = GeocellUtils.compute(bbox.getNorthEast(), GeocellManager.MAX_GEOCELL_RESOLUTION);
        String cellSW = GeocellUtils.compute(bbox.getSouthWest(), GeocellManager.MAX_GEOCELL_RESOLUTION);

        // The current lowest BBOX-search cost found; start with practical infinity.
        double minCost = Double.MAX_VALUE;

        // The set of cells having the lowest calculated BBOX-search cost.
        List<String> minCostCellSet = new ArrayList<String>();

        // First find the common prefix, if there is one.. this will be the base
        // resolution.. i.e. we don't have to look at any higher resolution cells.
        int minResolution = 0;
        int maxResoltuion = Math.min(cellNE.length(), cellSW.length());
        while (minResolution < maxResoltuion && cellNE.substring(0, minResolution + 1).startsWith(cellSW.substring(0, minResolution + 1))) {
            minResolution++;
        }

        // Iteravely calculate all possible sets of cells that wholely contain
        // the requested bounding box.
        for (int curResolution = minResolution; curResolution < GeocellManager.MAX_GEOCELL_RESOLUTION + 1; curResolution++) {
            String curNE = cellNE.substring(0, curResolution);
            String curSW = cellSW.substring(0, curResolution);

            int numCells = GeocellUtils.interpolationCount(curNE, curSW);
            if (numCells > MAX_FEASIBLE_BBOX_SEARCH_CELLS) {
                continue;
            }

            List<String> cellSet = GeocellUtils.interpolate(curNE, curSW);
            Collections.sort(cellSet);

            double cost;
            if (costFunction == null) {
                cost = DEFAULT_COST_FUNCTION.defaultCostFunction(cellSet.size(), curResolution);
            } else {
                cost = costFunction.defaultCostFunction(cellSet.size(), curResolution);
            }

            if (cost <= minCost) {
                minCost = cost;
                minCostCellSet = cellSet;
            } else {
                if (minCostCellSet.size() == 0) {
                    minCostCellSet = cellSet;
                }
                // Once the cost starts rising, we won't be able to do better, so abort.
                break;
            }
        }

        return minCostCellSet;
    }

    public static List<String> getMapCells(BoundingBox box, int geoCellSize) {
        LatLng nePoint = box.getNorthEast();
        LatLng swPoint = box.getSouthWest();
        String cellNE = GeocellUtils.compute(nePoint, geoCellSize);
        String cellSW = GeocellUtils.compute(swPoint, geoCellSize);

        return GeocellUtils.interpolate(cellNE, cellSW);
    }
}
