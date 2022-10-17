package localdatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mobv2.models.AddressImpl;
import com.example.mobv2.models.CommentImpl;
import com.example.mobv2.models.PostImpl;
import com.example.mobv2.models.UserImpl;

import localdatabase.daos.AddressDao;
import localdatabase.daos.CommentDao;
import localdatabase.daos.PostDao;
import localdatabase.daos.UserDao;

@Database(entities = {AddressImpl.class,
        UserImpl.class,
        PostImpl.class,
        CommentImpl.class}, version = 2)
public abstract class ApplicationDatabase extends RoomDatabase
{
    public abstract AddressDao addressDao();

    public abstract UserDao userDao();

    public abstract PostDao postDao();

    public abstract CommentDao commentDao();
}
