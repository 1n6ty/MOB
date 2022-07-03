package com.example.mobv2.models;

import androidx.annotation.NonNull;

import java.util.Map;

public class User
{
    private String nickName;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;

    public User(String name,
                String surname,
                String phoneNumber)
    {
        this(null, name, surname, null, phoneNumber);
    }

    public User(String nickName,
                String name,
                String surname,
                String email,
                String phoneNumber)
    {
        this.nickName = nickName;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getNickName()
    {
        return nickName;
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

    @NonNull
    @Override
    public String toString()
    {
        return name + " " + surname;
    }

    public static User parseFromMap(Map<String, Object> map)
    {
        if (map == null)
            return null;

        String nickName = (String) map.get("nick_name");
        String[] fullName = ((String) map.get("name")).split(" ");
        String name = fullName[0];
        String surname = fullName.length > 1 ? fullName[1] : "";
        String email = (String) map.get("email");
        String phoneNumber = (String) map.get("phone_number");
        return new User(nickName, name, surname, email, phoneNumber);
    }
}
