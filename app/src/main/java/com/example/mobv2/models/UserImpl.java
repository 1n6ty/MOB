package com.example.mobv2.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.mobv2.models.abstractions.User;
import com.example.mobv2.utils.abstractions.ParsableFromMap;

import java.util.Map;

@Entity
public class UserImpl implements User
{
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "userid")
    private String id;

    private String avatarUrl;
    private String nickName;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;

    @ColumnInfo(name = "usercurrent")
    private boolean current;

    @Ignore
    public UserImpl()
    {
    }

    public UserImpl(String id,
                    String avatarUrl,
                    String nickName,
                    String name,
                    String surname,
                    String email,
                    String phoneNumber)
    {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.nickName = nickName;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean compareById(User user)
    {
        if (user == null) return false;

        return id.equals(user.getId());
    }

    public static class UserBuilder implements ParsableFromMap<UserImpl>
    {
        private String id;

        private String avatarUrl;
        private String nickName;
        private String name;
        private String surname;
        private String email;
        private String phoneNumber;

        @NonNull
        @Override
        public UserImpl parseFromMap(@NonNull Map<String, Object> map)
        {
            parseIdFromMap(map);
            parseAvatarUrlFromMap(map);
            parseNickNameFromMap(map);
            parseFullNameFromMap(map);
            parseEmailFromMap(map);
            parsePhoneNumberFromMap(map);

            return new UserImpl(id, avatarUrl, nickName, name, surname, email, phoneNumber);
        }

        private void parseIdFromMap(Map<String, Object> map)
        {
            id = String.valueOf(((Double) map.get("id")).intValue());
        }

        private void parseAvatarUrlFromMap(Map<String, Object> map)
        {
            avatarUrl = (String) map.get("profile_img_url");
        }

        private void parseNickNameFromMap(Map<String, Object> map)
        {
            nickName = (String) map.get("nick_name");
        }

        private void parseFullNameFromMap(Map<String, Object> map)
        {
            var fullName = ((String) map.get("full_name")).split(" ");
            name = fullName[0];
            surname = fullName.length > 1 ? fullName[1] : "";
        }

        private void parseEmailFromMap(Map<String, Object> map)
        {
            email = (String) map.get("email");
        }

        private void parsePhoneNumberFromMap(Map<String, Object> map)
        {
            phoneNumber = (String) map.get("phone_number");
        }
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    @Override
    public String getNickName()
    {
        return nickName;
    }

    @Override
    public String getFullName()
    {
        return name + " " + surname;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getSurname()
    {
        return surname;
    }

    @Override
    public String getEmail()
    {
        return email;
    }

    @Override
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    @Override
    public boolean isCurrent()
    {
        return current;
    }

    @Override
    public void setCurrent(boolean current)
    {
        this.current = current;
    }
}
