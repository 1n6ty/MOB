package localdatabase.typeconverters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter
{
    @TypeConverter
    public Long timestampFromDate(Date date)
    {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public Date dateFromTimestamp(long timestamp)
    {
        return new Date(timestamp);
    }
}
