package localdatabase.typeconverters;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class LatLngConverter
{
    @TypeConverter
    public String stringFromPosition(LatLng position)
    {
        return position == null ? null : JsonParser.toJson(position);
    }

    @TypeConverter
    public LatLng positionFromString(String data)
    {
        Type type = new TypeToken<LatLng>()
        {
        }.getType();
        return data == null ? null : JsonParser.fromJson(data, type);
    }
}
