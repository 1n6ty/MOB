package com.example.mobv2.serverapi;

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

public class MOBServerAPI
{

    private final MOBInterface MOBAPI;

    public interface MOBAPICallback
    {
        public void funcOk(LinkedTreeMap<String, Object> obj);

        public void funcBad(LinkedTreeMap<String, Object> obj);

        public void fail(Throwable obj);
    }

    private Callback<LinkedTreeMap<String, Object>> createResponseCallback(MOBAPICallback obj)
    {
        return new Callback<LinkedTreeMap<String, Object>>()
        {
            @Override
            public void onResponse(@NonNull Call<LinkedTreeMap<String, Object>> call,
                                   @NonNull Response<LinkedTreeMap<String, Object>> response)
            {
                LinkedTreeMap<String, Object> body = new LinkedTreeMap<>();
                body.put("status_code", response.code());
                if (response.isSuccessful())
                {
                    body.put("response", response.body()
                                                 .get("response"));
                    obj.funcOk(body);
                }
                else
                {
                    try
                    {
                        LinkedTreeMap<String, Object> msg = new Gson().fromJson(response.errorBody()
                                                                                        .string(), new TypeToken<LinkedTreeMap<String, Object>>()
                        {
                        }.getType());
                        body.put("msg", msg.get("msg"));
                    }
                    catch (IOException e)
                    {
                        obj.fail(e.getCause());
                    }
                    obj.funcBad(body);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LinkedTreeMap<String, Object>> call,
                                  @NonNull Throwable t)
            {
                obj.fail(t);
            }
        };
    }

    public MOBServerAPI(String baseUrl)
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                                                  .addConverterFactory(GsonConverterFactory.create())
                                                  .build();
        MOBAPI = retrofit.create(MOBInterface.class);
    }

    public interface MOBInterface
    {
        @GET("auth/")
        Call<LinkedTreeMap<String, Object>> auth(@Query("l") String login,
                                                 @Query("p") String passwordHashed);

        @GET("getLocations/")
        Call<LinkedTreeMap<String, Object>> getLocations(@Query("token") String token);

        @GET("getMarks/")
        Call<LinkedTreeMap<String, Object>> getMarks(@Query("token") String token);

        @GET("getPost/")
        Call<LinkedTreeMap<String, Object>> getPost(@Query("post_id") int post_id,
                                                    @Query("token") String token);

        @GET("getComment/")
        Call<LinkedTreeMap<String, Object>> getComment(@Query("post_id") int post_id,
                                                       @Query("comment_id") int comment_id,
                                                       @Query("ind") boolean ind,
                                                       @Query("token") String token);

        @GET
        Call<LinkedTreeMap<String, Object>> refresh(@Query("token") String token,
                                                    @Query("token") String refresh);

        @GET
        Call<LinkedTreeMap<String, Object>> me(@Query("token") String token);

        @Multipart
        @POST("createPost/")
        Call<LinkedTreeMap<String, Object>> createPost(@Part("text") String text,
                                                       @Part("markx") double markx,
                                                       @Part("marky") double marky,
                                                       @Part("token") String token,
                                                       @Part MultipartBody.Part[] imgs);

        @FormUrlEncoded
        @POST("comment/")
        Call<LinkedTreeMap<String, Object>> comment(@Field("post_id") int post_id,
                                                    @Field("text") String text,
                                                    @Field("token") String token);

        @FormUrlEncoded
        @PUT("setLocation/")
        Call<LinkedTreeMap<String, Object>> setLocation(@Field("location_id") int location_id,
                                                        @Field("token") String token);

        @FormUrlEncoded
        @PUT("editUser/")
        Call<LinkedTreeMap<String, Object>> editUser(@Field("nickName") String nickName,
                                                     @Field("name") String name,
                                                     @Field("password") String password,
                                                     @Field("email") String email,
                                                     @Field("token") String token);

        @FormUrlEncoded
        @PUT("postInc/")
        Call<LinkedTreeMap<String, Object>> postInc(@Field("post_id") int post_id,
                                                    @Field("token") String token);

        @FormUrlEncoded
        @PUT("postDec/")
        Call<LinkedTreeMap<String, Object>> postDec(@Field("post_id") int post_id,
                                                    @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentInc/")
        Call<LinkedTreeMap<String, Object>> commentInc(@Field("post_id") int post_id,
                                                       @Field("comment_id") int comment_id,
                                                       @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentDec/")
        Call<LinkedTreeMap<String, Object>> commentDec(@Field("post_id") int post_id,
                                                       @Field("comment_id") int comment_id,
                                                       @Field("token") String token);

        @FormUrlEncoded
        @PUT("postReact/")
        Call<LinkedTreeMap<String, Object>> postReact(@Field("post_id") int post_id,
                                                      @Field("reaction") String reaction,
                                                      @Field("token") String token);

        @FormUrlEncoded
        @PUT("postUnreact/")
        Call<LinkedTreeMap<String, Object>> postUnreact(@Field("post_id") int post_id,
                                                        @Field("reaction") String reaction,
                                                        @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentReact/")
        Call<LinkedTreeMap<String, Object>> commentReact(@Field("post_id") int post_id,
                                                         @Field("comment_id") int comment_id,
                                                         @Field("reaction") String reaction,
                                                         @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentUnreact/")
        Call<LinkedTreeMap<String, Object>> commentUnreact(@Field("post_id") int post_id,
                                                           @Field("comment_id") int comment_id,
                                                           @Field("reaction") String reaction,
                                                           @Field("token") String token);

        @FormUrlEncoded
        @PUT("deleteComment/")
        Call<LinkedTreeMap<String, Object>> deleteComment(@Field("post_id") int post_id,
                                                          @Field("comment_id") int comment_id,
                                                          @Field("token") String token);

        @FormUrlEncoded
        @PUT("deletePost/")
        Call<LinkedTreeMap<String, Object>> deletePost(@Field("post_id") int post_id,
                                                       @Field("token") String token);
    }

    private static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    private static String toHexString(byte[] hash)
    {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public void refreshToken(MOBAPICallback obj,
                             String token,
                             String refreshToken)
    {
        Call<LinkedTreeMap<String, Object>> refreshCall = MOBAPI.refresh(token, refreshToken);
        refreshCall.enqueue(createResponseCallback(obj));
    }

    public void auth(MOBAPICallback obj,
                     String login,
                     String password) throws NoSuchAlgorithmException
    {
        Call<LinkedTreeMap<String, Object>> authCall =
                MOBAPI.auth(login, toHexString(getSHA(password)));
        authCall.enqueue(createResponseCallback(obj));
    }

    public void me(MOBAPICallback obj,
                   String token)
    {
        Call<LinkedTreeMap<String, Object>> meCall = MOBAPI.me(token);
        meCall.enqueue(createResponseCallback(obj));
    }

    public void getAddresses(MOBAPICallback obj,
                             String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getLocations(token);
        call.enqueue(createResponseCallback(obj));
    }

    public void getMarks(MOBAPICallback obj,
                         String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getMarks(token);
        call.enqueue(createResponseCallback(obj));
    }

    public void getPost(MOBAPICallback obj,
                        int postId,
                        String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getPost(postId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void getComment(MOBAPICallback obj,
                           int postId,
                           int commentId,
                           boolean ind,
                           String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.getComment(postId, commentId, ind, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void post(MOBAPICallback obj,
                     String text,
                     float markX,
                     float markY,
                     String[] imgPaths,
                     String token)
    {

        MultipartBody.Part[] imgs = new MultipartBody.Part[imgPaths.length];
        for (int i = 0; i < imgPaths.length; i++)
        {
            File file = new File(imgPaths[i]);
            imgs[i] =
                    MultipartBody.Part.createFormData("imgs", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }

        Call<LinkedTreeMap<String, Object>> call =
                MOBAPI.createPost(text, markX, markY, token, imgs);
        call.enqueue(createResponseCallback(obj));
    }

    public void comment(MOBAPICallback obj,
                        String text,
                        int postId,
                        String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.comment(postId, text, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void setAddress(MOBAPICallback obj,
                           int AddressId,
                           String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.setLocation(AddressId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void editUser(MOBAPICallback obj,
                         String name,
                         String nick,
                         String password,
                         String email,
                         String token) throws NoSuchAlgorithmException
    {
        Call<LinkedTreeMap<String, Object>> call =
                MOBAPI.editUser(nick, name, toHexString(getSHA(password)), email, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void postInc(MOBAPICallback obj,
                        int postId,
                        String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postInc(postId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void postDec(MOBAPICallback obj,
                        int postId,
                        String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postDec(postId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void commentInc(MOBAPICallback obj,
                           int postId,
                           int commentId,
                           String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentInc(postId, commentId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void commentDec(MOBAPICallback obj,
                           int postId,
                           int commentId,
                           String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.commentDec(postId, commentId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void postReact(MOBAPICallback obj,
                          int postId,
                          String reaction,
                          String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postReact(postId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void postUnreact(MOBAPICallback obj,
                            int postId,
                            String reaction,
                            String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.postUnreact(postId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void commentReact(MOBAPICallback obj,
                             int postId,
                             int commentId,
                             String reaction,
                             String token)
    {
        Call<LinkedTreeMap<String, Object>> call =
                MOBAPI.commentReact(postId, commentId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void commentUnreact(MOBAPICallback obj,
                               int postId,
                               int commentId,
                               String reaction,
                               String token)
    {
        Call<LinkedTreeMap<String, Object>> call =
                MOBAPI.commentUnreact(postId, commentId, reaction, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void postDelete(MOBAPICallback obj,
                           int postId,
                           String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.deletePost(postId, token);
        call.enqueue(createResponseCallback(obj));
    }

    public void commentDelete(MOBAPICallback obj,
                              int postId,
                              int commentId,
                              String token)
    {
        Call<LinkedTreeMap<String, Object>> call = MOBAPI.deleteComment(postId, commentId, token);
        call.enqueue(createResponseCallback(obj));
    }
}
