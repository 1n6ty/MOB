package com.example.mobv2.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.models.abstractions.Post;
import com.example.mobv2.models.abstractions.HavingCommentsIds;
import com.example.mobv2.utils.MyObservableArrayList;
import com.example.mobv2.utils.abstractions.ParsableFromMap;
import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import localdatabase.typeconverters.DateConverter;
import localdatabase.typeconverters.ListOfStringsConverter;
import localdatabase.typeconverters.ReactionsConverter;

@Entity
public class PostImpl implements Post, HavingCommentsIds
{
    @Ignore
    public static final int POST_ONLY_TEXT = 0, POST_ONLY_IMAGES = 1, POST_FULL = 2;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "postid")
    private String id;

    @Embedded
    private UserImpl user;
    @TypeConverters(DateConverter.class)
    private Date date;
    private String title;
    private String text;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> images;
    @TypeConverters(ReactionsConverter.class)
    private List<Reaction> reactions;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> commentsIds;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> positiveRates;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> negativeRates;

    @Ignore
    private final ObservableInt commentsCount;
    @Ignore
    private final ObservableInt ratesCount;

    @Ignore
    private final int type;

    public PostImpl(String id,
                    UserImpl user,
                    Date date,
                    String title,
                    String text,
                    List<String> images,
                    List<Reaction> reactions,
                    List<String> commentsIds,
                    List<String> positiveRates,
                    List<String> negativeRates)
    {
        this.id = id;
        this.title = title;
        this.user = user;
        this.date = date;
        this.text = text;
        this.images = images;

        if (images == null || images.isEmpty()) type = POST_ONLY_TEXT;
        else if (text == null || text.isEmpty()) type = POST_ONLY_IMAGES;
        else type = POST_FULL;

        this.reactions = reactions;
        this.commentsIds = new MyObservableArrayList<>(commentsIds);
        this.positiveRates = new MyObservableArrayList<>(positiveRates);
        this.negativeRates = new MyObservableArrayList<>(negativeRates);

        commentsCount = new ObservableInt(commentsIds.size());
        ((MyObservableArrayList<String>) this.commentsIds)
                .setOnListChangedCallback(new Operation(commentsCount, 1, -1));

        ratesCount = new ObservableInt(positiveRates.size() - negativeRates.size());
        ((MyObservableArrayList<String>) this.positiveRates)
                .setOnListChangedCallback(new Operation(ratesCount, 1, -1));

        ((MyObservableArrayList<String>) this.negativeRates)
                .setOnListChangedCallback(new Operation(ratesCount, -1, 1));
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
            user = new UserImpl.UserBuilder().parseFromMap(userMap);
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
            images = (ArrayList<String>) dataMap.get("img_urls");

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
    public List<String> getCommentsIds()
    {
        return commentsIds;
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

    public ObservableInt getCommentsCount()
    {
        return commentsCount;
    }

    public ObservableInt getRatesCount()
    {
        return ratesCount;
    }

    public int getType()
    {
        return type;
    }

    private static class Operation implements MyObservableArrayList.OnListChangedCallback<String>
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
        public void onAdded(String string)
        {
            observableInt.set(observableInt.get() + firstOperand);
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
        public void onRemoved(@Nullable Object o)
        {
            observableInt.set(observableInt.get() + secondOperand);
        }
    }
}
