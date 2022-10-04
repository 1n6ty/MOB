package com.example.mobv2.models;

import com.example.mobv2.utils.abstractions.ParsableFromMap;

import java.util.Map;

public class User
{
    private final String id;

    private final String avatarUrl;
    private final String nickName;
    private final String name;
    private final String surname;
    private final String email;
    private final String phoneNumber;

    private User(String id,
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

    public static class UserBuilder implements ParsableFromMap<User>
    {
        private String id;

        private String avatarUrl;
        private String nickName;
        private String name;
        private String surname;
        private String email;
        private String phoneNumber;

        @Override
        public User parseFromMap(Map<String, Object> map)
        {
            if (map == null)
                return null;

            parseIdFromMap(map);
            parseAvatarUrlFromMap(map);
            parseNickNameFromMap(map);
            parseFullNameFromMap(map);
            parseEmailFromMap(map);
            parsePhoneNumberFromMap(map);

            return new User(id, avatarUrl, nickName, name, surname, email, phoneNumber);
        }

        private void parseIdFromMap(Map<String, Object> map)
        {
            id = String.valueOf(((Double)map.get("id")).intValue());
        }

        private void parseAvatarUrlFromMap(Map<String, Object> map)
        {
            avatarUrl = (String) map.get("profile_img_url");
        }

        private void parseNickNameFromMap(Map<String, Object> map)
        {
            nickName = (String) map.get("nick");
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

    public String getId()
    {
        return id;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public String getNickName()
    {
        return nickName;
    }

    public String getFullname()
    {
        return name + " " + surname;
    }

    public String getName()
    {
        return name;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }
}
