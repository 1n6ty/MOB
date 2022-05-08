package com.example.mob;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class MOBServerAPI {

    private final MOBInterface MOBAPI;

    private Callback<LinkedTreeMap<String, Object>> createResponseCallback(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail){
        return new Callback<LinkedTreeMap<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<LinkedTreeMap<String, Object>> call, @NonNull Response<LinkedTreeMap<String, Object>> response) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    LinkedTreeMap<String, Object> body = new LinkedTreeMap<>();
                    body.put("status_code", response.code());
                    if (response.isSuccessful()) {
                        body.put("response", response.body().get("response"));
                        funcOk.apply(body);
                    } else {
                        try {
                            LinkedTreeMap<String, Object> msg =  new Gson().fromJson(response.errorBody().string(), new TypeToken<LinkedTreeMap<String, Object>>(){}.getType());
                            body.put("msg", msg.get("msg"));
                        } catch (IOException e) {
                            fail.apply(e.getCause());
                        }
                        funcBad.apply(body);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LinkedTreeMap<String, Object>> call, @NonNull Throwable t) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) fail.apply(t);
            }
        };
    }

    MOBServerAPI(String baseUrl){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        MOBAPI = retrofit.create(MOBInterface.class);
    }

    public interface MOBInterface{
        @GET("auth/")
        Call<LinkedTreeMap<String, Object>> auth(@Query("l") String login, @Query("p") String passwordHashed);
        @GET("getLocations/")
        Call<LinkedTreeMap<String, Object>> getLocations(@Query("token") String token);
        @GET("getMarks/")
        Call<LinkedTreeMap<String, Object>> getMarks(@Query("token") String token);
        @GET("getPost/")
        Call<LinkedTreeMap<String, Object>> getPost(@Query("post_id") int post_id, @Query("token") String token);
        @GET("getComment/")
        Call<LinkedTreeMap<String, Object>> getComment(@Query("post_id") int post_id, @Query("comment_id") int comment_id, @Query("token") String token);
        @GET
        Call<LinkedTreeMap<String, Object>> refresh(@Query("token") String token, @Query("token") String refresh);

        @Multipart
        @FormUrlEncoded
        @POST("createPost/")
        Call<LinkedTreeMap<String, Object>> createPost(@Field("text") String text,
                                                 @Field("markx") double markx,
                                                 @Field("marky") double marky,
                                                 @Part MultipartBody.Part[] imgs,
                                                 @Field("token") String token);
        @FormUrlEncoded
        @POST("comment/")
        Call<LinkedTreeMap<String, Object>> comment(@Field("post_id") int post_id, @Field("text") String text,
                           @Field("token") String token);

        @FormUrlEncoded
        @PUT("setLocation/")
        Call<LinkedTreeMap<String, Object>> setLocation(@Field("location_id") int location_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("editUser/")
        Call<LinkedTreeMap<String, Object>> editUser(@Field("nickName") String nickName, @Field("name") String name,
                                               @Field("password") String password,
                                               @Field("email") String email, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postInc/")
        Call<LinkedTreeMap<String, Object>> postInc(@Field("post_id") int post_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postDec/")
        Call<LinkedTreeMap<String, Object>> postDec(@Field("post_id") int post_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentInc/")
        Call<LinkedTreeMap<String, Object>> commentInc(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentDec/")
        Call<LinkedTreeMap<String, Object>> commentDec(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postReact/")
        Call<LinkedTreeMap<String, Object>> postReact(@Field("post_id") int post_id, @Field("reaction") String reaction, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postUnreact/")
        Call<LinkedTreeMap<String, Object>> postUnreact(@Field("post_id") int post_id, @Field("reaction") String reaction, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentReact/")
        Call<LinkedTreeMap<String, Object>> commentReact(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("reaction") String reaction, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentUnreact/")
        Call<LinkedTreeMap<String, Object>> commentUnreact(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("reaction") String reaction, @Field("token") String token);

        @FormUrlEncoded
        @PUT("deleteComment/")
        Call<LinkedTreeMap<String, Object>> deleteComment(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("deletePost/")
        Call<LinkedTreeMap<String, Object>> deletePost(@Field("post_id") int post_id, @Field("token") String token);
    }

    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String toHexString(byte[] hash){
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32){
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public void refreshToken(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail, String token, String refreshToken){
        Call<LinkedTreeMap<String, Object>> refreshCall = MOBAPI.refresh(token, refreshToken);
        refreshCall.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void auth(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
              String login, String password) throws NoSuchAlgorithmException {
        Call<LinkedTreeMap<String, Object>> authCall = MOBAPI.auth(login, toHexString(getSHA(password)));
        authCall.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void getLocations(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getLocations(token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void getMarks(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getMarks(token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void getPost(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 int postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getPost(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void getComment(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getComment(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }

    public void post(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    String text, float markX, float markY, String[] imgPaths, String token){

        MultipartBody.Part[] imgs = new MultipartBody.Part[imgPaths.length];
        for(int i = 0; i < imgPaths.length; i++){
            File file = new File(imgPaths[i]);
            imgs[i] = MultipartBody.Part.createFormData("imgs", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }

        Call<LinkedTreeMap<String, Object>> call = MOBAPI.createPost(text, markX, markY, imgs, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void comment(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 String text, int postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.comment(postId, text, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }

    public void setLocation(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                     int locationId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.setLocation(locationId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void editUser(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                  String name, String nick, String password, String email, String token) throws NoSuchAlgorithmException {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.editUser(nick, name, toHexString(getSHA(password)), email, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void postInc(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 int postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postInc(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void postDec(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 int postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postDec(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void commentInc(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentInc(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void commentDec(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentDec(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void postReact(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                   int postId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postReact(postId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void postUnreact(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                   int postId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postUnreact(postId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void commentReact(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                      int postId, int commentId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentReact(postId, commentId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void commentUnreact(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                      int postId, int commentId, String reaction, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentUnreact(postId, commentId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }

    public void postDelete(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.deletePost(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    public void commentDelete(Function<LinkedTreeMap<String, Object>, Void> funcOk, Function<LinkedTreeMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.deleteComment(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
}
