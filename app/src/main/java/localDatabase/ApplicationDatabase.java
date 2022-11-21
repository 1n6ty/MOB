package localDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mobv2.model.AddressImpl;
import com.example.mobv2.model.CommentImpl;
import com.example.mobv2.model.MarkerInfoImpl;
import com.example.mobv2.model.PostImpl;
import com.example.mobv2.model.UserImpl;

import localDatabase.dao.AddressDao;
import localDatabase.dao.CommentDao;
import localDatabase.dao.MarkerInfoDao;
import localDatabase.dao.PostDao;
import localDatabase.dao.UserDao;

@Database(entities = {AddressImpl.class,
        UserImpl.class,
        MarkerInfoImpl.class,
        PostImpl.class,
        CommentImpl.class}, version = 12)
public abstract class ApplicationDatabase extends RoomDatabase
{
    public abstract AddressDao addressDao();

    public abstract UserDao userDao();

    public abstract PostDao postDao();

    public abstract CommentDao commentDao();

    public abstract MarkerInfoDao markerInfoDao();
}
