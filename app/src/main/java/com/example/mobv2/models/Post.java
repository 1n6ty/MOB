package com.example.mobv2.models;

import android.graphics.Bitmap;

import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Post
{
    public static final int POST_ONLY_TEXT = 0, POST_ONLY_IMAGES = 1, POST_FULL = 2;

    private Bitmap avatar;
    private User user;
    private Date date;
    private String text;
    private List<String> images;

    private List<Reaction> reactions;

    private int type;

    public Post(Bitmap avatar,
                User user,
                Date date,
                String text,
                List<String> images,
                List<Reaction> reactions)
    {
        this.avatar = avatar;
        this.user = user;
        this.date = date;
        this.text = text;
        this.images = images;
        this.reactions = reactions;
        if (images == null || images.isEmpty())
            type = POST_ONLY_TEXT;
        else if (text == null || text.isEmpty())
            type = POST_ONLY_IMAGES;
        else
            type = POST_FULL;
    }

    public Bitmap getAvatar()
    {
        return avatar;
    }

    public User getUser()
    {
        return user;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Date getDate()
    {
        return date;
    }

    public List<Reaction> getReactions()
    {
        return reactions;
    }

    public int getType()
    {
        return type;
    }

    public static Post parseFromMap(Map<String, Object> map)
    {
        if (map == null)
            return null;

        // date
        Date date;
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = formatter.parse((String) Objects.requireNonNull(map.get("date")));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }

        // user
        LinkedTreeMap<String, Object> userMap = (LinkedTreeMap<String, Object>) map.get("user");
        User user = User.parseFromMap(userMap);

        // data
        LinkedTreeMap<String, Object> dataMap = (LinkedTreeMap<String, Object>) map.get("data");
        String text = (String) dataMap.get("text");
        ArrayList<String> imageUrls = (ArrayList<String>) dataMap.get("img_urls");

        // reactions
        ArrayList<Reaction> reactions = new ArrayList<>();
        LinkedTreeMap<String, Object> reactionsMap =
                (LinkedTreeMap<String, Object>) map.get("reactions");
        for (String key : reactionsMap.keySet())
        {
            Reaction reaction =
                    new Reaction(key, ((Double) reactionsMap.get(key)).intValue());
            reactions.add(reaction);
        }

        // appreciations


        return new Post(null, user, date, text, imageUrls, reactions);
    }
}
