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

import java.util.List;

import me.trashout.api.base.ApiBaseDataResult;
import me.trashout.model.CollectionPoint;
import me.trashout.model.Event;
import me.trashout.model.News;
import me.trashout.model.Trash;
import me.trashout.model.UserActivity;

/**
 * @author Miroslav Cupalka
 * @package me.trashout.api.result
 * @since 15.12.2016
 */
public class ApiGetHomeScreenDataResult extends ApiBaseDataResult {

    private List<Trash> trashList;
    private CollectionPoint collectionPointDustbin;
    private CollectionPoint collectionPointScrapyard;
    private List<UserActivity> dashboardUserActivityList;
    private News dashboardNews;
    private List<Event> dashboardEventList;

    private int statisticCleaned;
    private int statisticsReported;

    public ApiGetHomeScreenDataResult(List<Trash> trashList, CollectionPoint collectionPointDustbin, CollectionPoint collectionPointScrapyard, int statisticCleaned, int statisticsReported, List<UserActivity> dashboardUserActivityList, News dashboardNews, List<Event> dashboardEventList) {
        this.trashList = trashList;
        this.collectionPointDustbin = collectionPointDustbin;
        this.collectionPointScrapyard = collectionPointScrapyard;
        this.statisticCleaned = statisticCleaned;
        this.statisticsReported = statisticsReported;
        this.dashboardUserActivityList = dashboardUserActivityList;
        this.dashboardNews = dashboardNews;
        this.dashboardEventList = dashboardEventList;
    }

    public List<Trash> getTrashList() {
        return trashList;
    }

    public CollectionPoint getCollectionPointDustbin() {
        return collectionPointDustbin;
    }

    public CollectionPoint getCollectionPointScrapyard() {
        return collectionPointScrapyard;
    }

    public int getStatisticCleaned() {
        return statisticCleaned;
    }

    public int getStatisticsReported() {
        return statisticsReported;
    }

    public List<UserActivity> getDashboardUserActivityList() {
        return dashboardUserActivityList;
    }

    public News getDashboardNews() {
        return dashboardNews;
    }

    public List<Event> getDashboardEventList() {
        return dashboardEventList;
    }
}
