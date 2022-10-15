package localdatabase.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.mobv2.models.abstractions.User;

@Entity
public class UserEntity implements User
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

    public UserEntity(String id,
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
}
