package localDatabase.typeConverter;

import androidx.room.TypeConverter;

import com.example.mobv2.model.Reaction;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ReactionsConverter
{
    @TypeConverter
    public String jsonStringFromReactions(List<Reaction> reactions)
    {
        return reactions == null ? null : JsonParser.toJson(reactions);
    }

    @TypeConverter
    public List<Reaction> reactionsFromJsonString(String data)
    {
        Type type = new TypeToken<List<Reaction>>()
        {
        }.getType();
        return data == null ? null : JsonParser.fromJson(data, type);
    }
}
