package com.example.mobv2.models.abstractions;

import com.example.mobv2.utils.abstractions.Comparable;

public interface User extends Comparable<User>
{
    String getId();

    String getAvatarUrl();

    String getNickName();

    String getFullName();

    String getName();

    String getSurname();

    String getEmail();

    String getPhoneNumber();
}
