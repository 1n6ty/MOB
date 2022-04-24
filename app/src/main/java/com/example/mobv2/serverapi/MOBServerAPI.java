package com.example.mobv2.serverapi;

import com.example.mobv2.serverapi.callbacks.ArrayListSuccessCallback;
import com.example.mobv2.serverapi.callbacks.CreateSuccessCallback;
import com.example.mobv2.serverapi.callbacks.HashMapSuccessCallback;
import com.example.mobv2.serverapi.callbacks.StringSuccessCallback;
import com.example.mobv2.serverapi.callbacks.VoidSuccessCallback;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class MOBServerAPI
{
    private final MOBInterface MOBAPI;

    public MOBServerAPI(String baseUrl)
    {
        Retrofit retrofit =
                new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();
        MOBAPI = retrofit.create(MOBInterface.class);
    }

    public interface MOBInterface
    {
        @GET("auth/")
        Call<HashMap<String, String>> auth(@Query("l") String login,
                                           @Query("p") String passwordHashed);

        @GET("getLocations/")
        Call<HashMap<String, ArrayList<HashMap<String, String>>>> getLocations(@Query("token") String token);

        @GET("getMarks/")
        Call<HashMap<String, ArrayList<HashMap<String, String>>>> getMarks(@Query("token") String token);

        @GET("getPost/")
        Call<HashMap<String, Object>> getPost(@Query("post_id") int post_id,
                                              @Query("token") String token);

        @GET("getComment/")
        Call<HashMap<String, Object>> getComment(@Query("post_id") int post_id,
                                                 @Query("comment_id") int comment_id,
                                                 @Query("token") String token);

        @FormUrlEncoded
        @POST("createPost/")
        Call<HashMap<String, Integer>> createPost(@Field("text") String text,
                                                  @Field("img") String img,
                                                  @Field("markx") double markx,
                                                  @Field("marky") double marky,
                                                  @Field("token") String token);

        @FormUrlEncoded
        @POST("comment/")
        Call<HashMap<String, Integer>> comment(@Field("post_id") int post_id,
                                               @Field("text") String text,
                                               @Field("token") String token);

        @FormUrlEncoded
        @PUT("setLocation/")
        Call<HashMap<String, String>> setLocation(@Field("location_id") int location_id,
                                                  @Field("token") String token);

        @FormUrlEncoded
        @PUT("setUpdate/")
        Call<HashMap<String, String>> setUpdate(@Field("token") String token);

        @FormUrlEncoded
        @PUT("editUser/")
        Call<Void> editUser(@Field("nickName") String nickName, @Field("name") String name,
                            @Field("surname") String surname, @Field("password") String password,
                            @Field("email") String email, @Field("token") String token);

        @FormUrlEncoded
        @PUT("postInc/")
        Call<Void> postInc(@Field("post_id") int post_id, @Field("token") String token);

        @FormUrlEncoded
        @PUT("postDec/")
        Call<Void> postDec(@Field("post_id") int post_id, @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentInc/")
        Call<Void> commentInc(@Field("post_id") int post_id,
                              @Field("comment_id") int comment_id,
                              @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentDec/")
        Call<Void> commentDec(@Field("post_id") int post_id,
                              @Field("comment_id") int comment_id,
                              @Field("token") String token);

        @FormUrlEncoded
        @PUT("postReact/")
        Call<Void> postReact(@Field("post_id") int post_id,
                             @Field("reaction") String reaction,
                             @Field("token") String token);

        @FormUrlEncoded
        @PUT("postUnreact/")
        Call<Void> postUnreact(@Field("post_id") int post_id,
                               @Field("reaction") String reaction,
                               @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentReact/")
        Call<Void> commentReact(@Field("post_id") int post_id,
                                @Field("comment_id") int comment_id,
                                @Field("reaction") String reaction,
                                @Field("token") String token);

        @FormUrlEncoded
        @PUT("commentUnreact/")
        Call<Void> commentUnreact(@Field("post_id") int post_id,
                                  @Field("comment_id") int comment_id,
                                  @Field("reaction") String reaction,
                                  @Field("token") String token);

        @FormUrlEncoded
        @PUT("deleteComment/")
        Call<Void> deleteComment(@Field("post_id") int post_id,
                                 @Field("comment_id") int comment_id,
                                 @Field("token") String token);

        @FormUrlEncoded
        @PUT("deletePost/")
        Call<Void> deletePost(@Field("post_id") int post_id, @Field("token") String token);
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

    public void auth(Function<String, Void> funcOk,
                     Function<Integer, Void> funcBad,
                     String login,
                     String password) throws NoSuchAlgorithmException
    {
        Call<HashMap<String, String>> authCall = MOBAPI.auth(login, toHexString(getSHA(password)));
        authCall.enqueue(new StringSuccessCallback(funcOk, funcBad));
    }

    public void getLocations(Function<ArrayList<HashMap<String, String>>, Void> funcOk,
                             Function<Integer, Void> funcBad,
                             String token)
    {
        Call<HashMap<String, ArrayList<HashMap<String, String>>>> getFunc =
                MOBAPI.getLocations(token);
        getFunc.enqueue(new ArrayListSuccessCallback(funcOk, funcBad));
    }

    public void getMarks(Function<ArrayList<HashMap<String, String>>, Void> funcOk,
                         Function<Integer, Void> funcBad,
                         String token)
    {
        Call<HashMap<String, ArrayList<HashMap<String, String>>>> getFunc = MOBAPI.getMarks(token);
        getFunc.enqueue(new ArrayListSuccessCallback(funcOk, funcBad));
    }

    public void getPost(Function<HashMap<String, Object>, Void> funcOk,
                        Function<Integer, Void> funcBad,
                        int post_id,
                        String token)
    {
        Call<HashMap<String, Object>> getFunc = MOBAPI.getPost(post_id, token);
        getFunc.enqueue(new HashMapSuccessCallback(funcOk, funcBad));
    }

    public void getComment(Function<HashMap<String, Object>, Void> funcOk,
                           Function<Integer, Void> funcBad,
                           int post_id,
                           int comment_id,
                           String token)
    {
        Call<HashMap<String, Object>> getFunc = MOBAPI.getComment(post_id, comment_id, token);
        getFunc.enqueue(new HashMapSuccessCallback(funcOk, funcBad));
    }

    public void createPost(Function<Integer, Void> funcOk,
                           Function<Integer, Void> funcBad,
                           String text,
                           String img,
                           double markx,
                           double marky,
                           String token)
    {
        Call<HashMap<String, Integer>> crpost = MOBAPI.createPost(text, img, markx, marky, token);
        crpost.enqueue(new CreateSuccessCallback(funcOk, funcBad));
    }

    public void comment(Function<Integer, Void> funcOk,
                        Function<Integer, Void> funcBad,
                        int post_id,
                        String text,
                        String token)
    {
        Call<HashMap<String, Integer>> crcomment = MOBAPI.comment(post_id, text, token);
        crcomment.enqueue(new CreateSuccessCallback(funcOk, funcBad));
    }

    public void setLocation(Function<String, Void> funcOk,
                            Function<Integer, Void> funcBad,
                            int location_id,
                            String token)
    {
        Call<HashMap<String, String>> setL = MOBAPI.setLocation(location_id, token);
        setL.enqueue(new StringSuccessCallback(funcOk, funcBad));
    }

    public void setUpdate(Function<String, Void> funcOk,
                          Function<Integer, Void> funcBad,
                          String token)
    {
        Call<HashMap<String, String>> setU = MOBAPI.setUpdate(token);
        setU.enqueue(new StringSuccessCallback(funcOk, funcBad));
    }

    public void editUser(Function<Integer, Void> funcOk,
                         Function<Integer, Void> funcBad,
                         String nickName,
                         String name,
                         String surname,
                         String password,
                         String email,
                         String token) throws NoSuchAlgorithmException
    {
        Call<Void> editU =
                MOBAPI.editUser(nickName, name, surname, toHexString(getSHA(password)), email, token);
        editU.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void postInc(Function<Integer, Void> funcOk,
                        Function<Integer, Void> funcBad,
                        int post_id,
                        String token)
    {
        Call<Void> postinc = MOBAPI.postInc(post_id, token);
        postinc.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void postDec(Function<Integer, Void> funcOk,
                        Function<Integer, Void> funcBad,
                        int post_id,
                        String token)
    {
        Call<Void> postdec = MOBAPI.postDec(post_id, token);
        postdec.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void commentInc(Function<Integer, Void> funcOk,
                           Function<Integer, Void> funcBad,
                           int post_id,
                           int comment_id,
                           String token)
    {
        Call<Void> commentinc = MOBAPI.commentInc(post_id, comment_id, token);
        commentinc.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void commentDec(Function<Integer, Void> funcOk,
                           Function<Integer, Void> funcBad,
                           int post_id,
                           int comment_id,
                           String token)
    {
        Call<Void> commentdec = MOBAPI.commentDec(post_id, comment_id, token);
        commentdec.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void postReact(Function<Integer, Void> funcOk,
                          Function<Integer, Void> funcBad,
                          int post_id,
                          String reaction,
                          String token)
    {
        Call<Void> postr = MOBAPI.postReact(post_id, reaction, token);
        postr.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void postUnreact(Function<Integer, Void> funcOk,
                            Function<Integer, Void> funcBad,
                            int post_id,
                            String reaction,
                            String token)
    {
        Call<Void> postunr = MOBAPI.postUnreact(post_id, reaction, token);
        postunr.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void commentReact(Function<Integer, Void> funcOk,
                             Function<Integer, Void> funcBad,
                             int post_id,
                             int comment_id,
                             String reaction,
                             String token)
    {
        Call<Void> commentr = MOBAPI.commentReact(post_id, comment_id, reaction, token);
        commentr.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void commentUnreact(Function<Integer, Void> funcOk,
                               Function<Integer, Void> funcBad,
                               int post_id,
                               int comment_id,
                               String reaction,
                               String token)
    {
        Call<Void> commentunr = MOBAPI.commentUnreact(post_id, comment_id, reaction, token);
        commentunr.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void deleteComment(Function<Integer, Void> funcOk,
                              Function<Integer, Void> funcBad,
                              int post_id,
                              int comment_id,
                              String token)
    {
        Call<Void> deletec = MOBAPI.deleteComment(post_id, comment_id, token);
        deletec.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }

    public void deletePost(Function<Integer, Void> funcOk,
                           Function<Integer, Void> funcBad,
                           int post_id,
                           String token)
    {
        Call<Void> deletep = MOBAPI.deletePost(post_id, token);
        deletep.enqueue(new VoidSuccessCallback(funcOk, funcBad));
    }
}
