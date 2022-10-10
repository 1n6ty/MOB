package com.example.mobv2.models;

import com.example.mobv2.models.abstractions.Takable;
import com.example.mobv2.utils.abstractions.ParsableFromMap;
import com.google.gson.internal.LinkedTreeMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Comment implements Takable
{
    private final String id;

    private final User user;
    private final Date date;
    private final String text;
    private final List<Reaction> reactions;
    private final List<String> commentsIds;
    private final List<String> positiveRates;
    private final List<String> negativeRates;

    private Comment(String id,
                    User user,
                    Date date,
                    String text,
                    List<Reaction> reactions,
                    List<String> commentsIds,
                    List<String> positiveRates,
                    List<String> negativeRates)
    {
        this.id = id;
        this.user = user;
        this.date = date;
        this.text = text;
        this.reactions = reactions;
        this.commentsIds = commentsIds;
        this.positiveRates = positiveRates;
        this.negativeRates = negativeRates;
    }

    public static Comment createNewComment(String id,
                                           User user,
                                           String text)
    {
        return new Comment(id, user, new Date(), text,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }

    public static final class CommentBuilder implements ParsableFromMap<Comment>
    {
        private String id;

        private User user;
        private Date date;
        private String text;
        private List<Reaction> reactions;
        private List<String> commentsIds;
        private List<String> positiveRates;
        private List<String> negativeRates;

        @Override
        public Comment parseFromMap(Map<String, Object> map)
        {
            if (map == null)
                return null;

            parseDateFromMap(map);
            parseIdFromMap(map);
            parseUserFromMap(map);
            parseDataFromMap(map);
            parseReactionsFromMap(map);
            parseRateFromMap(map);

            return new Comment(id, user, date, text, reactions, commentsIds, positiveRates, negativeRates);
        }

        private void parseIdFromMap(Map<String, Object> map)
        {
            id = String.valueOf(((Double) map.get("id")).intValue());
        }

        private void parseUserFromMap(Map<String, Object> map)
        {
            var userMap = (LinkedTreeMap<String, Object>) map.get("user");
            user = new User.UserBuilder().parseFromMap(userMap);
        }

        private void parseDateFromMap(Map<String, Object> map)
        {
            try
            {
                var formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                date = formatter.parse((String) Objects.requireNonNull(map.get("date")));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        private void parseDataFromMap(Map<String, Object> map)
        {
            var dataMap = (LinkedTreeMap<String, Object>) map.get("data");
            text = (String) dataMap.get("content");

            commentsIds = new ArrayList<>();
            for (var commentId : (ArrayList<Double>) dataMap.get("comment_ids"))
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
    }

    public String getId()
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

    public List<String> getCommentsIds()
    {
        return commentsIds;
    }

    public List<String> getPositiveRates()
    {
        return positiveRates;
    }

    public List<String> getNegativeRates()
    {
        return negativeRates;
    }
}
