package com.example.mobv2.model;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.model.abstraction.HavingCommentsIds;
import com.example.mobv2.model.abstraction.UserContent;
import com.example.mobv2.model.abstraction.Post;
import com.example.mobv2.util.MyObservableArrayList;
import com.example.mobv2.util.abstraction.ParsableFromMap;
import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import localDatabase.typeConverter.DateConverter;
import localDatabase.typeConverter.ReactionsConverter;
import localDatabase.typeConverter.StringListConverter;

@Entity
public class PostImpl implements Post, HavingCommentsIds, UserContent
{
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "postid")
    private final String id;

    @Embedded
    private final UserImpl user;
    @TypeConverters(DateConverter.class)
    private final Date date;
    private final String title;
    private final String text;
    @TypeConverters(StringListConverter.class)
    private final List<String> images;
    @TypeConverters(ReactionsConverter.class)
    private final List<Reaction> reactions;
    @TypeConverters(StringListConverter.class)
    private final List<String> commentIds;
    @TypeConverters(StringListConverter.class)
    private final List<String> positiveRates;
    @TypeConverters(StringListConverter.class)
    private final List<String> negativeRates;

    public PostImpl(String id,
                    UserImpl user,
                    Date date,
                    String title,
                    String text,
                    List<String> images,
                    List<Reaction> reactions,
                    List<String> commentIds,
                    List<String> positiveRates,
                    List<String> negativeRates)
    {
        this.id = id;
        this.title = title;
        this.user = user;
        this.date = date;
        this.text = text;
        this.images = images;
        this.reactions = reactions;
        this.commentIds = commentIds;
        this.positiveRates = positiveRates;
        this.negativeRates = negativeRates;
    }

    public static final class PostBuilder implements ParsableFromMap<PostImpl>
    {
        private String id;

        private UserImpl user;
        private Date date;
        private String text;
        private List<Reaction> reactions;
        private List<String> commentsIds;
        private List<String> positiveRates;
        private List<String> negativeRates;
        private String title;
        private List<String> images;

        @NonNull
        @Override
        public PostImpl parseFromMap(@NonNull Map<String, Object> map)
        {
            parseDateFromMap(map);
            parseIdFromMap(map);
            parseUserFromMap(map);
            parseDataFromMap(map);
            parseReactionsFromMap(map);
            parseRateFromMap(map);

            return new PostImpl(id, user, date, title, text, images, reactions, commentsIds, positiveRates, negativeRates);
        }

        private void parseIdFromMap(Map<String, Object> map)
        {
            id = String.valueOf(((Double) getDataMap(map).get("id")).intValue());
        }

        private void parseUserFromMap(Map<String, Object> map)
        {
            var userMap = (LinkedTreeMap<String, Object>) map.get("user");
            user = new UserImpl.UserParser().parseFromMap(userMap);
        }

        private void parseDateFromMap(Map<String, Object> map)
        {
            try
            {
                var formatter = new SimpleDateFormat("dd.MM.yyyy/HH:mm", Locale.getDefault());
                date = formatter.parse((String) ((getDataMap(map).get("public_date"))));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        private void parseDataFromMap(Map<String, Object> map)
        {
            var dataMap = getDataMap(map);
            title = (String) dataMap.get("title");
            text = (String) dataMap.get("content");
            images = (ArrayList<String>) dataMap.get("image_urls");

            commentsIds = new ArrayList<>();
            var rawCommentIds = (ArrayList<Double>) dataMap.get("comment_ids");
            for (var commentId : rawCommentIds)
                commentsIds.add(String.valueOf(commentId.intValue()));
        }

        private void parseReactionsFromMap(Map<String, Object> map)
        {
            var reactionsMap = (LinkedTreeMap<String, ArrayList<Double>>) map.get("reactions");
            reactions = new ArrayList<>();
            for (var key : reactionsMap.keySet())
            {
                var userIdsWhoLiked = new ArrayList<String>();
                for (var userId : reactionsMap.get(key))
                    userIdsWhoLiked.add(String.valueOf(userId.intValue()));

                var reaction = new Reaction(key, userIdsWhoLiked);
                reactions.add(reaction);
            }
        }

        private void parseRateFromMap(Map<String, Object> map)
        {
            var rateMap = (LinkedTreeMap<String, ArrayList<Double>>) map.get("rate");
            positiveRates = new ArrayList<>();
            for (Double userId : rateMap.get("p"))
                positiveRates.add(String.valueOf(userId.intValue()));

            negativeRates = new ArrayList<>();
            for (Double userId : rateMap.get("m"))
                negativeRates.add(String.valueOf(userId.intValue()));
        }

        private LinkedTreeMap<String, Object> getDataMap(Map<String, Object> map)
        {
            return (LinkedTreeMap<String, Object>) map.get("data");
        }
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public UserImpl getUser()
    {
        return user;
    }

    @Override
    public Date getDate()
    {
        return date;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public String getText()
    {
        return text;
    }

    @Override
    public List<String> getImages()
    {
        return images;
    }

    @Override
    public List<Reaction> getReactions()
    {
        return reactions;
    }

    @Override
    public List<String> getCommentIds()
    {
        return commentIds;
    }

    @Override
    public List<String> getPositiveRates()
    {
        return positiveRates;
    }

    @Override
    public List<String> getNegativeRates()
    {
        return negativeRates;
    }

    public static class Operation extends MyObservableArrayList.OnListChangedCallback<String>
    {
        private final ObservableInt observableInt;
        private final int firstOperand;
        private final int secondOperand;

        public Operation(ObservableInt observableInt,
                         int firstOperand,
                         int secondOperand)
        {
            this.observableInt = observableInt;
            this.firstOperand = firstOperand;
            this.secondOperand = secondOperand;
        }

        @Override
        public void onAdded(int index,
                            String element)
        {
            observableInt.set(observableInt.get() + firstOperand);
        }

        @Override
        public void onRemoved(int index)
        {
            observableInt.set(observableInt.get() + secondOperand);
        }

        @Override
        public void onRemoved(int index,
                              Object o)
        {
            observableInt.set(observableInt.get() + secondOperand);
        }
    }
}
