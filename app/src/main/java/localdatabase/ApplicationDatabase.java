package localdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import localdatabase.daos.AddressDao;
import localdatabase.daos.CommentDao;
import localdatabase.daos.PostDao;
import localdatabase.daos.UserDao;
import localdatabase.models.AddressEntity;
import localdatabase.models.CommentEntity;
import localdatabase.models.PostEntity;
import localdatabase.models.UserEntity;

@Database(entities = {AddressEntity.class,
        UserEntity.class,
        PostEntity.class,
        CommentEntity.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase
{
    public abstract AddressDao addressDao();

    public abstract UserDao userDao();

    public abstract PostDao postDao();

    public abstract CommentDao commentDao();
}
