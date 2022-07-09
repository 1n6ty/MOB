package com.example.mobv2.models;

import java.util.Map;

public class User
{
    private final int id;

    private final String avatarUrl;
    private final String nickName;
    private final String name;
    private final String surname;
    private final String email;
    private final String phoneNumber;

    public User(int id,
                String avatarUrl,
                String name,
                String surname,
                String phoneNumber)
    {
        this(id, avatarUrl, null, name, surname, null, phoneNumber);
    }

    public User(int id,
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

    public static User parseFromMap(Map<String, Object> map)
    {
        if (map == null)
            return null;

        int id = ((Double) map.get("id")).intValue();

       var avatarUrl = (String) map.get("profile_img_url");
       var nickName = (String) map.get("nick_name");
       var fullName = ((String) map.get("name")).split(" ");
       var name = fullName[0];
       var surname = fullName.length > 1 ? fullName[1] : "";
       var email = (String) map.get("email");
       var phoneNumber = (String) map.get("phone_number");

        return new User(id, avatarUrl, nickName, name, surname, email, phoneNumber);
    }

    public int getId()
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
