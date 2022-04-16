package com.example.mobv2.databaseimprovisation;

import com.example.mobv2.models.Post;
import com.example.mobv2.models.Reaction;
import com.example.mobv2.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Database
{
    public static List<User> usersDb = new ArrayList<User>()
    {
        {
            {
                add(new User("Ivan", "Ivanov", ""));
            }
            {
                add(new User("John", "Smith", ""));
            }
            {
                add(new User("Natasha", "Petrova", ""));
            }
        }
    };


    public static List<Post> postsDb = new ArrayList<Post>()
    {
        {
            add(new Post(null, usersDb.get(0), new Date(), new ArrayList<Reaction>()
            {{
                add(new Reaction(Reaction.EMOJI_LIKE, new Random().nextInt(1000)));
                add(new Reaction(Reaction.EMOJI_DISLIKE, new Random().nextInt(1000)));
                add(new Reaction(Reaction.EMOJI_LOVE, new Random().nextInt(1000)));
            }}));
            add(new Post(null, usersDb.get(1), new Date(), new ArrayList<Reaction>()
            {{
                add(new Reaction(Reaction.EMOJI_LIKE, new Random().nextInt(1000)));
                add(new Reaction(Reaction.EMOJI_DISLIKE, new Random().nextInt(10)));
            }}));

            add(new Post(null, usersDb.get(2), new Date(), new ArrayList<Reaction>()
            {{
                add(new Reaction(Reaction.EMOJI_LIKE, new Random().nextInt(100)));
                add(new Reaction(Reaction.EMOJI_DISLIKE, new Random().nextInt(10000)));
            }}));
        }
    };
}
