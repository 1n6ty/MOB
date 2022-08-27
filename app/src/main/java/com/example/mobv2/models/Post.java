package com.example.mobv2.models;

import androidx.databinding.ObservableInt;

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

    private final int id;

    private final String title;
    private final User user;
    private final Date date;
    private final String text;
    private final List<String> images;
    private final List<Reaction> reactions;
    private final ObservableInt commentsCount;
    private final ObservableInt appreciationsCount;
    private int appreciated;

    private final int type;

    public Post(int id,
                String title,
                User user,
                Date date,
                String text,
                List<String> images,
                List<Reaction> reactions,
                int commentsCount,
                int appreciationsCount,
                int appreciated)
    {
        this.id = id;
        this.title = title;
        this.user = user;
        this.date = date;
        this.text = text;
        this.images = images;
        if (images == null || images.isEmpty())
            type = POST_ONLY_TEXT;
        else if (text == null || text.isEmpty())
            type = POST_ONLY_IMAGES;
        else
            type = POST_FULL;
        this.reactions = reactions;
        this.commentsCount = new ObservableInt(commentsCount);
        this.appreciationsCount = new ObservableInt(appreciationsCount);
        this.appreciated = appreciated;
    }

    public static Post parseFromMap(Map<String, Object> map)
    {
        if (map == null)
            return null;

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

        int id = ((Double) map.get("id")).intValue();

        var title = (String) map.get("title");
        var userMap = (LinkedTreeMap<String, Object>) map.get("user");
        var user = User.parseFromMap(userMap);
        var dataMap = (LinkedTreeMap<String, Object>) map.get("data");
        var text = (String) dataMap.get("text");
        var imageUrls = (ArrayList<String>) dataMap.get("img_urls");
        var reactions = new ArrayList<Reaction>();
        var reactionsMap = (LinkedTreeMap<String, ArrayList<Double>>) map.get("reactions");
        for (var key : reactionsMap.keySet())
        {
            var usersWhoLiked = new ArrayList<Integer>();
            for (var userId : reactionsMap.get(key))
                usersWhoLiked.add(userId.intValue());

            var reaction = new Reaction(key, usersWhoLiked);
            reactions.add(reaction);
        }

        // appreciations
        var appreciationsCount = ((Double) map.get("appreciations")).intValue();
        var appreciatedByUser = ((Double) map.get("appreciated")).intValue();

        var commentsCount = ((Double) map.get("comments_count")).intValue();

        return new Post(id, title, user, date, text, imageUrls, reactions, commentsCount, appreciationsCount, appreciatedByUser);
    }

    public int getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public User getUser()
    {
        return user;
    }

    public String getText()
    {
        return text;
    }

    public Date getDate()
    {
        return date;
    }

    public List<Reaction> getReactions()
    {
        return reactions;
    }

    public List<String> getImages()
    {
        return images;
    }

    public ObservableInt getCommentsCount()
    {
        return commentsCount;
    }

    public ObservableInt getAppreciationsCount()
    {
        return appreciationsCount;
    }

    public int getAppreciated()
    {
        return appreciated;
    }

    public void setAppreciated(int appreciated)
    {
        this.appreciated = appreciated;
    }

    public int getType()
    {
        return type;
    }
}
