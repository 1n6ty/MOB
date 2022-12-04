package com.example.mobv2.util;

import com.example.mobv2.R;
import com.example.mobv2.ui.activity.mainActivity.MainActivity;

import java.util.Date;

public abstract class DateString
{
    private final MainActivity mainActivity;

    public DateString(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public String getDateString()
    {
        var dateString = new StringBuilder();

        var todayDate = new Date();
        var date = getDate();
        if (date.getDate() == todayDate.getDate() && date.getMonth() == todayDate.getMonth())
        {
            dateString.append(mainActivity.getString(R.string.today_at)).append(" ");
            dateString.append(fillZeros(date.getHours())).append(":");
            dateString.append(fillZeros(date.getMinutes())).append(":");
            dateString.append(fillZeros(date.getSeconds()));
        }
        else if (date.getDate() == todayDate.getDate() - 1 && date.getMonth() == todayDate.getMonth() - 1)
        {
            dateString.append(mainActivity.getString(R.string.yesterday_at)).append(" ");
            dateString.append(fillZeros(date.getHours())).append(":");
            dateString.append(fillZeros(date.getMinutes())).append(":");
            dateString.append(fillZeros(date.getSeconds()));
        }
        else
        {
            String month = mainActivity.getResources()
                                       .getStringArray(R.array.months)[date.getMonth() - 1];
            dateString.append(date.getDate()).append(" ");
            dateString.append(month.substring(0, 3)).append(" ");
            dateString.append(date.getYear() + 1900);
        }


        return dateString.toString();
    }

    private String fillZeros(int number)
    {
        return String.format(MainActivity.LOCALE, "%02d", number);
    }

    protected abstract Date getDate();
}
