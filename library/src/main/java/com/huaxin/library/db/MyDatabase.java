package com.huaxin.library.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WatchRecord.class,Download.class},version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public static MyDatabase instance;
    public static MyDatabase getInstance(Context context){
        if(instance == null){
            synchronized (MyDatabase.class){
                if(instance == null){
                    instance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class
                            ,"user_database").allowMainThreadQueries().
                            build();
                }
            }
        }
        return instance;
    }

    public abstract WatchDao watchDao();
    public abstract DownloadDao downloadDao();

}
