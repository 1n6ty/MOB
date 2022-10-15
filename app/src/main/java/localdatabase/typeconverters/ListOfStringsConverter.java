package localdatabase.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListOfStringsConverter
{
    @TypeConverter
    public String stringFromListOfString(List<String> commentsIds)
    {
        return commentsIds == null ? null : JsonParser.toJson(commentsIds);
    }

    @TypeConverter
    public List<String> listOfStringsFromString(String data)
    {
        Type type = new TypeToken<List<String>>()
        {
        }.getType();
        return data == null ? null : JsonParser.fromJson(data, type);
    }
}
