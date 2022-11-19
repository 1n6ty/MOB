package com.example.mobv2.model;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableInt;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.model.abstraction.Comment;
import com.example.mobv2.model.abstraction.HavingCommentsIds;
import com.example.mobv2.model.abstraction.UserContent;
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
public class CommentImpl implements Comment, HavingCommentsIds, UserContent
{
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "commentid")
    private final String id;

    @Embedded
    private final UserImpl user;
    @TypeConverters(DateConverter.class)
    private final Date date;
    private final String text;
    @TypeConverters(ReactionsConverter.class)
    private final List<Reaction> reactions;
    @TypeConverters(StringListConverter.class)
    private final List<String> commentIds;
    @TypeConverters(StringListConverter.class)
    private final List<String> positiveRates;
    @TypeConverters(StringListConverter.class)
    private final List<String> negativeRates;

    @Ignore
    private final ObservableInt commentsCount;
    @Ignore
    private final ObservableInt ratesCount;

    public CommentImpl(String id,
                       UserImpl user,
                       Date date,
                       String text,
                       List<Reaction> reactions,
                       List<String> commentIds,
                       List<String> positiveRates,
                       List<String> negativeRates)
    {
        this.id = id;
        this.user = user;
        this.date = date;
        this.text = text;
        this.reactions = reactions;

        this.commentIds = new MyObservableArrayList<>(commentIds);
        this.positiveRates = new MyObservableArrayList<>(positiveRates);
        this.negativeRates = new MyObservableArrayList<>(negativeRates);

        // TODO FIX IT PLEASE
        commentsCount = new ObservableInt(commentIds.size());
        ((MyObservableArrayList<String>) this.commentIds)
                .setOnListChangedCallback(new PostImpl.Operation(commentsCount, 1, -1));

        ratesCount = new ObservableInt(positiveRates.size() - negativeRates.size());
        ((MyObservableArrayList<String>) this.positiveRates)
                .setOnListChangedCallback(new PostImpl.Operation(ratesCount, 1, -1));

        ((MyObservableArrayList<String>) this.negativeRates)
                .setOnListChangedCallback(new PostImpl.Operation(ratesCount, -1, 1));
    }

    public static CommentImpl createNewComment(String id,
                                               UserImpl user,
                                               String text)
    {
        return new CommentImpl(id, user, new Date(), text, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public static final class CommentBuilder implements ParsableFromMap<CommentImpl>
    {
        private String id;

        private UserImpl user;
        private Date date;
        private String text;
        private List<Reaction> reactions;
        private List<String> commentsIds;
        private List<String> positiveRates;
        private List<String> negativeRates;

        @NonNull
        @Override
        public CommentImpl parseFromMap(@NonNull Map<String, Object> map)
        {
            parseDateFromMap(map);
            parseIdFromMap(map);
            parseUserFromMap(map);
            parseDataFromMap(map);
            parseReactionsFromMap(map);
            parseRateFromMap(map);

            return new CommentImpl(id, user, date, text, reactions, commentsIds, positiveRates, negativeRates);
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
            text = (String) dataMap.get("content");

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
    public String getText()
    {
        return text;
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

    public ObservableInt getCommentsCount()
    {
        return commentsCount;
    }

    public ObservableInt getRatesCount()
    {
        return ratesCount;
    }
}
