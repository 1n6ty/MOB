package com.example.mobv2.model.abstraction;

import com.example.mobv2.util.abstraction.Comparable;

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

    boolean isCurrent();

    void setCurrent(boolean current);
}