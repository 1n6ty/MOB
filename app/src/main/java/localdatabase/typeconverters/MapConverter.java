package localdatabase.typeconverters;

import androidx.room.TypeConverter;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapConverter
{
    @TypeConverter
    public String stringFromMap(Map<String, Object> map)
    {
        return map == null ? null : JsonParser.toJson(map);
    }

    @TypeConverter
    public Map<String, Object> mapFromString(String data)
    {
        Type type = new TypeToken<Map<String, Object>>()
        {
        }.getType();
        return data == null ? null : JsonParser.fromJson(data, type);
    }
}
