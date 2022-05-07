package com.example.mob;

import android.os.Build;

import androidx.annotation.NonNull;

import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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

    private Callback<HashMap<String, Object>> createResponseCallback(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail){
        return new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<HashMap<String, Object>> call, @NonNull Response<HashMap<String, Object>> response) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    HashMap<String, Object> body = new HashMap<String, Object>();
                    body.put("status_code", response.code());
                    if (response.isSuccessful()) {
                        body.put("response", response.body().get("response"));
                        funcOk.apply(body);
                    } else {
                        body.put("msg", response.body().get("msg"));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HashMap<String, Object>> call, @NonNull Throwable t) {
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
        Call<HashMap<String, Object>> auth(@Query("l") String login, @Query("p") String passwordHashed);
        @GET("getLocations/")
        Call<HashMap<String, Object>> getLocations(@Query("token") String token);
        @GET("getMarks/")
        Call<HashMap<String, Object>> getMarks(@Query("token") String token);
        @GET("getPost/")
        Call<HashMap<String, Object>> getPost(@Query("post_id") int post_id, @Query("token") String token);
        @GET("getComment/")
        Call<HashMap<String, Object>> getComment(@Query("post_id") int post_id, @Query("comment_id") int comment_id, @Query("token") String token);
        @GET
        Call<HashMap<String, Object>> refresh(@Query("token") String token, @Query("token") String refresh);

        @Multipart
        @FormUrlEncoded
        @POST("createPost/")
        Call<HashMap<String, Object>> createPost(@Field("text") String text,
                                                 @Field("markx") double markx,
                                                 @Field("marky") double marky,
                                                 @Part MultipartBody.Part[] imgs,
                                                 @Field("token") String token);
        @FormUrlEncoded
        @POST("comment/")
        Call<HashMap<String, Object>> comment(@Field("post_id") int post_id, @Field("text") String text,
                           @Field("token") String token);

        @FormUrlEncoded
        @PUT("setLocation/")
        Call<HashMap<String, Object>> setLocation(@Field("location_id") int location_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("editUser/")
        Call<HashMap<String, Object>> editUser(@Field("nickName") String nickName, @Field("name") String name,
                                               @Field("password") String password,
                                               @Field("email") String email, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postInc/")
        Call<HashMap<String, Object>> postInc(@Field("post_id") int post_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postDec/")
        Call<HashMap<String, Object>> postDec(@Field("post_id") int post_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentInc/")
        Call<HashMap<String, Object>> commentInc(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentDec/")
        Call<HashMap<String, Object>> commentDec(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postReact/")
        Call<HashMap<String, Object>> postReact(@Field("post_id") int post_id, @Field("reaction") String reaction, @Field("token") String token);
        @FormUrlEncoded
        @PUT("postUnreact/")
        Call<HashMap<String, Object>> postUnreact(@Field("post_id") int post_id, @Field("reaction") String reaction, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentReact/")
        Call<HashMap<String, Object>> commentReact(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("reaction") String reaction, @Field("token") String token);
        @FormUrlEncoded
        @PUT("commentUnreact/")
        Call<HashMap<String, Object>> commentUnreact(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("reaction") String reaction, @Field("token") String token);

        @FormUrlEncoded
        @PUT("deleteComment/")
        Call<HashMap<String, Object>> deleteComment(@Field("post_id") int post_id, @Field("comment_id") int comment_id, @Field("token") String token);
        @FormUrlEncoded
        @PUT("deletePost/")
        Call<HashMap<String, Object>> deletePost(@Field("post_id") int post_id, @Field("token") String token);
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

    void refreshToken(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail, String token, String refreshToken){
        Call<HashMap<String, Object>> refreshCall = MOBAPI.refresh(token, refreshToken);
        refreshCall.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void auth(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
              String login, String password) throws NoSuchAlgorithmException {
        Call<HashMap<String, Object>> authCall = MOBAPI.auth(login, toHexString(getSHA(password)));
        authCall.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void getLocations(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail, String token){
        Call<HashMap<String, Object>> call = MOBAPI.getLocations(token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void getMarks(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail, String token){
        Call<HashMap<String, Object>> call = MOBAPI.getMarks(token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void getPost(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 int postId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.getPost(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void getComment(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.getComment(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }

    void post(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    String text, float markX, float markY, String[] imgPaths, String token){

        MultipartBody.Part[] imgs = new MultipartBody.Part[imgPaths.length];
        for(int i = 0; i < imgPaths.length; i++){
            File file = new File(imgPaths[i]);
            imgs[i] = MultipartBody.Part.createFormData("imgs", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }

        Call<HashMap<String, Object>> call = MOBAPI.createPost(text, markX, markY, imgs, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void comment(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 String text, int postId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.comment(postId, text, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }

    void setLocation(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                     int locationId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.setLocation(locationId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void editUser(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                  String name, String nick, String password, String email, String token) throws NoSuchAlgorithmException {
        Call<HashMap<String, Object>> call = MOBAPI.editUser(nick, name, toHexString(getSHA(password)), email, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void postInc(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 int postId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.postInc(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void postDec(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                 int postId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.postDec(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void commentInc(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.commentInc(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void commentDec(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.commentDec(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void postReact(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                   int postId, String reaction, String token){
        Call<HashMap<String, Object>> call = MOBAPI.postReact(postId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void postUnreact(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                   int postId, String reaction, String token){
        Call<HashMap<String, Object>> call = MOBAPI.postUnreact(postId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void commentReact(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                      int postId, int commentId, String reaction, String token){
        Call<HashMap<String, Object>> call = MOBAPI.commentReact(postId, commentId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void commentUnreact(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                      int postId, int commentId, String reaction, String token){
        Call<HashMap<String, Object>> call = MOBAPI.commentUnreact(postId, commentId, reaction, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }

    void postDelete(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.deletePost(postId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
    void commentDelete(Function<HashMap<String, Object>, Void> funcOk, Function<HashMap<String, Object>, Void> funcBad, Function<Throwable, Void> fail,
                    int postId, int commentId, String token){
        Call<HashMap<String, Object>> call = MOBAPI.deleteComment(postId, commentId, token);
        call.enqueue(createResponseCallback(funcOk, funcBad, fail));
    }
}
