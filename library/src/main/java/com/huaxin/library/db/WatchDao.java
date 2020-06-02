package com.huaxin.library.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(WatchRecord watchRecord);

    @Query("select * from watch ORDER BY timestamp DESC")
    List<WatchRecord> getWatchList();

    @Query("select count(*) from watch ")
    int getWatchCount();

    @Query("delete from watch where id IN (:ids)")
    void deleteAll(List<Long> ids);

    //    @Update
//    void updateWatch(WatchRecord... watchRecords);
    @Delete
    int deleteAll(WatchRecord... watchRecords);

    //    @Delete("delete * from table order by id DESC limit 1")
    @Delete
    void delete(WatchRecord watchRecord);

    @Query("SELECT * FROM watch WHERE timestamp BETWEEN :minAge AND :maxAge ORDER BY timestamp DESC")
    List<WatchRecord> getWatchList(long minAge, long maxAge);

    @Query("SELECT * FROM watch WHERE timestamp < (:time) ")
    List<WatchRecord> getWatchList(long time);
}
