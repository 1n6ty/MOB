package localDatabase.typeConverter;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StringListConverter
{
    @TypeConverter
    public String stringFromStringList(List<String> stringList)
    {
        return stringList == null ? null : JsonParser.toJson(stringList);
    }

    @TypeConverter
    public List<String> stringListFromString(String data)
    {
        Type type = new TypeToken<List<String>>()
        {
        }.getType();
        return data == null ? null : JsonParser.fromJson(data, type);
    }
}
