package com.example.mobv2.models;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableInt;

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
import java.util.Objects;

public class Post
{
    public static final int POST_ONLY_TEXT = 0, POST_ONLY_IMAGES = 1, POST_FULL = 2;

    private final String id;

    private final User user;
    private final Date date;
    private final String text;
    private final List<Reaction> reactions;
    private final MyObservableArrayList<String> commentsIds;
    private final MyObservableArrayList<String> positiveRates;
    private final MyObservableArrayList<String> negativeRates;

    private final String title;
    private final List<String> images;

    private final int type;

    private final ObservableInt commentsCount;
    private final ObservableInt appreciationsCount;

    private Post(String id,
                 User user,
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

        if (images == null || images.isEmpty())
            type = POST_ONLY_TEXT;
        else if (text == null || text.isEmpty())
            type = POST_ONLY_IMAGES;
        else
            type = POST_FULL;

        this.reactions = reactions;
        this.commentsIds = new MyObservableArrayList<>(commentsIds);
        this.positiveRates = new MyObservableArrayList<>(positiveRates);
        this.negativeRates = new MyObservableArrayList<>(negativeRates);

        commentsCount = new ObservableInt(commentsIds.size());
        this.commentsIds.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(String string)
            {
                commentsCount.set(commentsCount.get() + 1);
            }

            @Override
            public void onAdded(int index,
                                String element)
            {
                commentsCount.set(commentsCount.get() + 1);
            }

            @Override
            public void onRemoved(int index)
            {
                commentsCount.set(commentsCount.get() - 1);
            }

            @Override
            public void onRemoved(@Nullable Object o)
            {
                commentsCount.set(commentsCount.get() - 1);
            }
        });

        appreciationsCount = new ObservableInt(positiveRates.size() - negativeRates.size());
        this.positiveRates.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(String string)
            {
                appreciationsCount.set(appreciationsCount.get() + 1);
            }

            @Override
            public void onAdded(int index,
                                String element)
            {
                appreciationsCount.set(appreciationsCount.get() + 1);
            }

            @Override
            public void onRemoved(int index)
            {
                appreciationsCount.set(appreciationsCount.get() - 1);
            }

            @Override
            public void onRemoved(@Nullable Object o)
            {
                appreciationsCount.set(appreciationsCount.get() - 1);
            }
        });

        this.negativeRates.setOnListChangedCallback(new MyObservableArrayList.OnListChangedCallback<>()
        {
            @Override
            public void onAdded(String string)
            {
                appreciationsCount.set(appreciationsCount.get() - 1);
            }

            @Override
            public void onAdded(int index,
                                String element)
            {
                appreciationsCount.set(appreciationsCount.get() - 1);
            }

            @Override
            public void onRemoved(int index)
            {
                appreciationsCount.set(appreciationsCount.get() + 1);
            }

            @Override
            public void onRemoved(@Nullable Object o)
            {
                appreciationsCount.set(appreciationsCount.get() + 1);
            }
        });
    }

    public String getTitle()
    {
        return title;
    }

    public List<String> getImages()
    {
        return images;
    }

    public int getType()
    {
        return type;
    }

    public static final class PostBuilder implements ParsableFromMap<Post>
    {
        private String id;

        private User user;
        private Date date;
        private String text;
        private List<Reaction> reactions;
        private List<String> commentsIds;
        private List<String> positiveRates;
        private List<String> negativeRates;
        private String title;
        private List<String> images;

        @Override
        public Post parseFromMap(Map<String, Object> map)
        {
            if (map == null)
                return null;

            parseDateFromMap(map);
            parseIdFromMap(map);
            parseUserFromMap(map);
            parseDataFromMap(map);
            parseReactionsFromMap(map);
            parseRateFromMap(map);

            return new Post(id, user, date, title, text, images, reactions, commentsIds, positiveRates, negativeRates);
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
            title = (String) dataMap.get("title");
            text = (String) dataMap.get("content");
            images = (ArrayList<String>) dataMap.get("img_urls");

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

    public ObservableInt getCommentsCount()
    {
        return commentsCount;
    }

    public ObservableInt getAppreciationsCount()
    {
        return appreciationsCount;
    }
}
