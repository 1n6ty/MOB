package com.example.testapp;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.Path;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class MOBServerAPI {

    private final MOBInterface MOBAPI;

    interface MOBAPICallback{
        public void funcOk(LinkedTreeMap<String, Object> obj);
        public void funcBad(LinkedTreeMap<String, Object> obj);
        public void fail(Throwable obj);
    }

    private Callback<LinkedTreeMap<String, Object>> createResponseCallback(MOBAPICallback obj){
        return new Callback<LinkedTreeMap<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<LinkedTreeMap<String, Object>> call, @NonNull Response<LinkedTreeMap<String, Object>> response) {
                LinkedTreeMap<String, Object> body = new LinkedTreeMap<>();
                body.put("status_code", response.code());
                if (response.isSuccessful()) {
                    body.put("response", response.body().get("response"));
                    obj.funcOk(body);
                } else {
                    try {
                        LinkedTreeMap<String, Object> msg =  new Gson().fromJson(response.errorBody().string(), new TypeToken<LinkedTreeMap<String, Object>>(){}.getType());
                        body.put("msg", msg.get("msg"));
                    } catch (IOException e) {
                        obj.fail(e.getCause());
                    }
                    obj.funcBad(body);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LinkedTreeMap<String, Object>> call, @NonNull Throwable t) {
                obj.fail(t);
            }
        };
    }

    MOBServerAPI(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        MOBAPI = retrofit.create(MOBInterface.class);
    }

    public interface MOBInterface{
        @GET("urbaAPI/user/auth/")
        Call<LinkedTreeMap<String, Object>> auth(@Query("l") String login, @Query("p") String password);
        @GET("urbaAPI/user/get/")
        Call<LinkedTreeMap<String, Object>> getUserProfile(@Query("user_id") String user_id, @Header("Authorization") String token);
        @GET("urbaAPI/user/me/")
        Call<LinkedTreeMap<String, Object>> getMe(@Header("Authorization") String token);
        @GET("urbaAPI/address/marks/get/")
        Call<LinkedTreeMap<String, Object>> getMarks(@Header("Authorization") String token);
        @GET("urbaAPI/post/get/")
        Call<LinkedTreeMap<String, Object>> getPost(@Query("post_id") String post_id, @Header("Authorization") String token);
        @GET("urbaAPI/comment/get/")
        Call<LinkedTreeMap<String, Object>> getComment(@Query("comment_id") String comment_id, @Header("Authorization") String token);
        @GET("urbaAPI/user/refresh/")
        Call<LinkedTreeMap<String, Object>> refresh(@Query("refresh") String refresh, @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("urbaAPI/post/create/")
        Call<LinkedTreeMap<String, Object>> createPostWithoutImage(@Field("title") String title,
                                                       @Field("content") String text,
                                                       @Field("markx") double markx,
                                                       @Field("marky") double marky,
                                                       @Header("Authorization") String token);
        @Multipart
        @POST("urbaAPI/post/create/")
        Call<LinkedTreeMap<String, Object>> createPostWithImage(@Part("title") String title,
                                                                   @Part("content") String text,
                                                                   @Part("markx") double markx,
                                                                   @Part("marky") double marky,
                                                                   @Part MultipartBody.Part[] images,
                                                                   @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/comment/create/")
        Call<LinkedTreeMap<String, Object>> comment(@Field("parent_id") String parent_id, @Field("content") String content, @Field("parent") String litera,
                                                    @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("urbaAPI/address/set/")
        Call<LinkedTreeMap<String, Object>> setLocation(@Field("location_id") String location_id, @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserNickName(@Field("nick") String nickName, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserName(@Field("full_name") String name, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserPassword(@Field("password") String password, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserEmail(@Field("email") String email, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserPhone(@Field("phone_number") String phone_number, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserBio(@Field("Bio") String bio, @Header("Authorization") String token);
        @Multipart
        @POST("urbaAPI/user/edit/")
        Call<LinkedTreeMap<String, Object>> editUserProfileImg(@Part MultipartBody.Part img, @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("urbaAPI/post/rate/increment/")
        Call<LinkedTreeMap<String, Object>> postInc(@Field("post_id") String post_id, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/post/rate/decrement/")
        Call<LinkedTreeMap<String, Object>> postDec(@Field("post_id") String post_id, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/comment/rate/increment/")
        Call<LinkedTreeMap<String, Object>> commentInc(@Field("comment_id") String comment_id, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/comment/rate/decrement/")
        Call<LinkedTreeMap<String, Object>> commentDec(@Field("comment_id") String comment_id, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/post/reactions/add/")
        Call<LinkedTreeMap<String, Object>> postReact(@Field("post_id") String post_id, @Field("reaction") String reaction, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/post/reactions/remove/")
        Call<LinkedTreeMap<String, Object>> postUnreact(@Field("post_id") String post_id, @Field("reaction") String reaction, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/comment/reactions/add/")
        Call<LinkedTreeMap<String, Object>> commentReact(@Field("comment_id") String comment_id, @Field("reaction") String reaction, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/comment/reactions/remove/")
        Call<LinkedTreeMap<String, Object>> commentUnreact(@Field("comment_id") String comment_id, @Field("reaction") String reaction, @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("urbaAPI/comment/delete/")
        Call<LinkedTreeMap<String, Object>> deleteComment(@Field("comment_id") String comment_id, @Header("Authorization") String token);
        @FormUrlEncoded
        @POST("urbaAPI/post/delete/")
        Call<LinkedTreeMap<String, Object>> deletePost(@Field("post_id") String post_id, @Header("Authorization") String token);

        @FormUrlEncoded
        @POST("urbaAPI/address/create/")
        Call<LinkedTreeMap<String, Object>> createAddress(@Field("country") String country, @Field("city") String city,
                                                            @Field("street") String street, @Field("house") String house, @Header("Authorization") String token);
        @POST("urbaAPI/address/join/{id}/")
        Call<LinkedTreeMap<String, Object>> joinAddress(@Path("id") String address_id, @Header("Authorization") String token);
        @POST("urbaAPI/address/leave/{id}/")
        Call<LinkedTreeMap<String, Object>> leaveAddress(@Path("id") String address_id, @Header("Authorization") String token);
    }

    public void refreshToken(MOBAPICallback obj,
                             String token, String refreshToken){
        Call<LinkedTreeMap<String, Object>> refreshCall = MOBAPI.refresh(refreshToken, token);
        refreshCall.enqueue(createResponseCallback(obj));
    }
    public void auth(MOBAPICallback obj,
              String login, String password){
        Call<LinkedTreeMap<String, Object>> authCall = MOBAPI.auth(login, password);
        authCall.enqueue(createResponseCallback(obj));
    }
    public void getUserProfile(MOBAPICallback obj,
                               String token, String user_id){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getUserProfile(user_id, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void me(MOBAPICallback obj,
                             String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getMe(token);
        call.enqueue(createResponseCallback(obj));
    }
    public void getMarks(MOBAPICallback obj,
                         String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getMarks(token);
        call.enqueue(createResponseCallback(obj));
    }
    public void getPost(MOBAPICallback obj,
                 String postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getPost(postId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void getComment(MOBAPICallback obj,
                    String commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getComment(commentId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void post(MOBAPICallback obj,
                    String content, String title, double markX, double markY, File [] files, String token){
        if(files != null) {
            MultipartBody.Part[] imgs = new MultipartBody.Part[files];
            for(int i = 0; i < files.length; i++){
                imgs[i] = MultipartBody.Part.createFormData("images", files[i].getName(), RequestBody.create(MediaType.parse("image/*"), files[i]));
            }
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.createPostWithImage(title, content, markX, markY, imgs, token);
            call.enqueue(createResponseCallback(obj));
        } else{
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.createPostWithoutImage(title, content, markX, markY, token);
            call.enqueue(createResponseCallback(obj));
        }
    }
    public void commentPost(MOBAPICallback obj,
                 String text, String parentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.comment(parentId, text, "p", token);
        call.enqueue(createResponseCallback(obj));
    }
    public void commentComment(MOBAPICallback obj,
                 String text, String parentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.comment(parentId, text, "c", token);
        call.enqueue(createResponseCallback(obj));
    }

    public void setLocation(MOBAPICallback obj,
                     String locationId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.setLocation(locationId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void editUser(MOBAPICallback obj,
                         String name, String nick_name, String password, String email, String bio, String phone_number, File new_profile_img, String token){
        if(name != null){
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserName(name, token);
            call.enqueue(createResponseCallback(obj));
        }
        if(bio != null){
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserBio(name, token);
            call.enqueue(createResponseCallback(obj));
        }
        if(nick_name != null){
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserNickName(nick_name, token);
            call.enqueue(createResponseCallback(obj));
        }
        if(password != null){
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserPassword(password, token);
            call.enqueue(createResponseCallback(obj));
        }
        if(email != null){
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserEmail(email, token);
            call.enqueue(createResponseCallback(obj));
        }
        if(phone_number != null){
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserPhone(phone_number, token);
            call.enqueue(createResponseCallback(obj));
        }
        if(file != null){
            MultipartBody.Part img = MultipartBody.Part.createFormData("new_profile_img", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUserProfileImg(img, token);
            call.enqueue(createResponseCallback(obj));
        }
    }
    public void postInc(MOBAPICallback obj,
                 String postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postInc(postId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void postDec(MOBAPICallback obj,
                 String postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postDec(postId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void commentInc(MOBAPICallback obj,
                    String commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentInc(commentId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void commentDec(MOBAPICallback obj,
                    String commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentDec(commentId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void postReact(MOBAPICallback obj,
                   String postId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postReact(postId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void postUnreact(MOBAPICallback obj,
                   String postId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postUnreact(postId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void commentReact(MOBAPICallback obj,
                      String commentId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentReact(commentId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void commentUnreact(MOBAPICallback obj,
                      String commentId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentUnreact(commentId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void postDelete(MOBAPICallback obj,
                    String postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.deletePost(postId, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void commentDelete(MOBAPICallback obj,
                    String commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.deleteComment(commentId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void createAddress(MOBAPICallback obj,
                    String country, String city,
                    String street, String house, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.createAddress(country, city, street, house, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void joinAddress(MOBAPICallback obj,
                    String address_id, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.joinAddress(address_id, token);
        call.enqueue(createResponseCallback(obj));
    }
    public void leaveAddress(MOBAPICallback obj,
                    String address_id, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.leaveAddress(address_id, token);
        call.enqueue(createResponseCallback(obj));
    }
}
