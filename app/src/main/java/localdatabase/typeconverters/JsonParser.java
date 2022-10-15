package localdatabase.typeconverters;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonParser
{
    private static final Gson gson = new Gson();

    public static <T> T fromJson(String json,
                                 Type type)
    {
        return gson.fromJson(json, type);
    }

    public static <T> String toJson(T obj)
    {
        return gson.toJson(obj);
    }
}
