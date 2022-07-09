package com.example.mobv2.models;

import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Comment
{
    private final int id;

    private final User user;
    private final Date date;
    private final String text;
    private final List<Reaction> reactions;

    public Comment(int id,
                   User user,
                   Date date,
                   String text,
                   List<Reaction> reactions)
    {
        this.id = id;
        this.user = user;
        this.date = date;
        this.text = text;
        this.reactions = reactions;
    }

    public static Comment parseFromMap(Map<String, Object> map)
    {
        if (map == null)
            return null;

        Date date;
        try
        {
            var formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = formatter.parse((String) Objects.requireNonNull(map.get("date")));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }

        int id = ((Double) map.get("id")).intValue();

        var userMap = (LinkedTreeMap<String, Object>) map.get("user");
        var user = User.parseFromMap(userMap);
        var text = (String) map.get("text");
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

        //

        return new Comment(id, user, date, text, reactions);
    }

    public int getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public Date getDate()
    {
        return date;
    }

    public String getText()
    {
        return text;
    }

    public List<Reaction> getReactions()
    {
        return reactions;
    }
}
