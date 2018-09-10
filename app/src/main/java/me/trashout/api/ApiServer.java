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

package me.trashout.api;

import java.util.List;
import java.util.Map;

import me.trashout.api.param.ApiCreateCollectionPointNewSpamParam;
import me.trashout.api.param.ApiCreateEventNewSpamParam;
import me.trashout.api.param.ApiCreateTrashNewSpamParam;
import me.trashout.api.param.ApiJoinUserToEventParam;
import me.trashout.api.result.ApiGetTrashCountResult;
import me.trashout.model.Area;
import me.trashout.model.Badge;
import me.trashout.model.CollectionPoint;
import me.trashout.model.Event;
import me.trashout.model.News;
import me.trashout.model.NewsDetail;
import me.trashout.model.Organization;
import me.trashout.model.Trash;
import me.trashout.model.User;
import me.trashout.model.UserActivity;
import me.trashout.model.UserDevice;
import me.trashout.model.ZoomPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiServer {

    // TRASH
    @GET("/v1/trash/")
    Call<List<Trash>> getTrashList(@QueryMap Map<String, String> options);

    @GET("/v1/trash/{id}")
    Call<Trash> getTrashDetail(@Path("id") long id);

    @PUT("/v1/trash/{id}")
    Call<ResponseBody> updateTrash(@Path("id") long id, @Body Trash trash);

    @POST("/v1/trash/")
    Call<ResponseBody> createTrash(@Body Trash trash);

    @GET("/v1/trash/zoom-point/")
    Call<List<ZoomPoint>> getZoomPointList(@QueryMap Map<String, String> options);

    @POST("/v1/spam/trash/")
    Call<ResponseBody> createTrashNewSpam(@Body ApiCreateTrashNewSpamParam apiCreateTrashNewSpamParam);

    @GET("/v1/trash/count/")
    Call<ApiGetTrashCountResult> getTrashCount(@Query("trashStatus") String options, @Query("geoAreaCountry") String country);

    // EVENT
    @POST("/v1/event/")
    Call<ResponseBody> createEvent(@Body Event event);

    @POST("/v1/spam/event/")
    Call<ResponseBody> createEventNewSpam(@Body ApiCreateEventNewSpamParam apiCreateEventNewSpamParam);

    @GET("/v1/event/{id}")
    Call<Event> getEventDetail(@Path("id") long id);

    @PUT("/v1/event/{id}")
    Call<ResponseBody> updateEvent(@Path("id") long id, @Body Event event);

    @POST("/v1/event/{id}/users")
    Call<ResponseBody> joinUserToEvent(@Path("id") long id, @Body ApiJoinUserToEventParam apiJoinUserToEventParam);

    @GET("/v1/event/?limit=3&orderBy=start,gps")
    Call<List<Event>> getEventList(@Query("startFrom") String timeBoundaryFrom, @Query("startTo") String timeBoundaryTo, @Query("userPosition") String userPosition, @Query("attributesNeeded") String attributesNeeded);

    // USER
    @POST("/v1/user/")
    Call<User> createUser(@Body User user);

    @GET("/v1/user/me/")
    Call<User> getUserByFirebaseToken();

    @PUT("/v1/user/{id}")
    Call<ResponseBody> updateUser(@Path("id") long id, @Body User user);

    @GET("/v1/user/{id}")
    Call<User> getUserById(@Path("id") long id);

    @GET("/v1/user/{id}/userActivity")
    Call<List<UserActivity>> getUserActivity(@Path("id") Long id);

    @GET("/v1/user/{id}/activity")
    Call<List<UserActivity>> getUsersActivity(@Path("id") Long id);

    @POST("/v1/user/devices")
    Call<UserDevice> createUserDevice(@Body UserDevice userDevice);

    @DELETE("/v1/user/devices/{tokenFCM}")
    Call<ResponseBody> deleteUserDevice(@Path("tokenFCM") String tokenFcm);


    // COLLECTION POINT
    @GET("/v1/collection-point/")
    Call<List<CollectionPoint>> getCollectionPointList(@QueryMap Map<String, String> options);

    @GET("/v1/collection-point/{id}")
    Call<CollectionPoint> getCollectionPointDetail(@Path("id") long id);

    @POST("/v1/spam/collection-point/")
    Call<ResponseBody> createCollectionPointNewSpam(@Body ApiCreateCollectionPointNewSpamParam apiCreateCollectionPointNewSpamParam);

    // NEWS
    @GET("/v1/prContent")/*?language=cs_CZ*/
    Call<List<News>> getNewsList(@Query("language") String language,@Query("page") int page, @Query("limit") int limit, @Query("orderBy") String orderBy);

    @GET("/v1/prContent/{id}")
    Call<NewsDetail> getNewsDetail(@Path("id") long id);

    // AREA
    @GET("/v1/area?type=country")
    Call<List<Area>> getAreaList();

    // ORGANIZATIONS
    @GET("/v1/organization")
    Call<List<Organization>> getOrganizationsList(@Query("page") int page, @Query("limit") int limit, @Query("orderBy") String orderBy);

    @GET("/v1/organization")
    Call<List<Organization>> getOrganizationsList(@Query("orderBy") String orderBy);

    // BADGE
    @GET("/v1/badge")
    Call<List<Badge>> getBadgeList();
}
