package localdatabase.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.mobv2.models.Reaction;
import com.example.mobv2.models.abstractions.Post;

import java.util.Date;
import java.util.List;

import localdatabase.typeconverters.DateConverter;
import localdatabase.typeconverters.ListOfStringsConverter;
import localdatabase.typeconverters.ReactionsConverter;

@Entity
public class PostEntity implements Post
{
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "postid")
    private String id;

    @Embedded
    private UserEntity user;
    @TypeConverters(DateConverter.class)
    private Date date;
    private String text;
    @TypeConverters(ReactionsConverter.class)
    private List<Reaction> reactions;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> commentsIds;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> positiveRates;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> negativeRates;

    private String title;
    @TypeConverters(ListOfStringsConverter.class)
    private List<String> images;

    public PostEntity(String id,
                      UserEntity user,
                      Date date,
                      String text,
                      List<Reaction> reactions,
                      List<String> commentsIds,
                      List<String> positiveRates,
                      List<String> negativeRates,
                      String title,
                      List<String> images)
    {
        this.id = id;
        this.user = user;
        this.date = date;
        this.text = text;
        this.reactions = reactions;
        this.commentsIds = commentsIds;
        this.positiveRates = positiveRates;
        this.negativeRates = negativeRates;
        this.title = title;
        this.images = images;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public UserEntity getUser()
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
}
