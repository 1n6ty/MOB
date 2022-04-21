package com.example.mobv2.models;

import androidx.annotation.NonNull;

public class User
{
    private String name;
    private String surname;
    private String number;

    public User(String name,
                String surname,
                String number)
    {
        this.name = name;
        this.surname = surname;
        this.number = number;
    }

    public String getName()
    {
        return name;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getNumber()
    {
        return number;
    }

    @NonNull
    @Override
    public String toString()
    {
        return name + " " + surname;
    }
}
