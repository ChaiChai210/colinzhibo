package com.huaxin.library.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Download Download);

    @Query("select * from download")
    List<Download> getDownloadList();

    @Query("select count(*) from download where url = :url")
    int getWatchCount(String url);

    @Query("select count(*) from download ")
    int getWatchCount();

    @Query("delete from download where id IN (:ids)")
    void deleteAll(List<Integer> ids);

    //    @Update
//    void updateWatch(Download... Downloads);
    @Delete
    int deleteAll(Download... Downloads);

    //    @Delete("delete * from table order by id DESC limit 1")
    @Delete
    void delete(Download Download);
}
